package com.autentia.tutoriales.reservas.teatro.infra.task;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class TaskScheduler implements AutoCloseable {

    private final Map<String, ScheduledFuture<?>> tasks;
    private final ThreadPoolTaskScheduler scheduler;

    public TaskScheduler() {
        tasks = new ConcurrentHashMap<>();
        scheduler = new TaskSchedulerBuilder().build();
        scheduler.initialize();
    }

    public void scheduleTask(final String type, final UUID id, final Runnable task, final int startAfterSeconds) {
        final var key = buildKey(type, id);
        tasks.computeIfAbsent(key, k -> scheduler.schedule(wrap(k, task), Instant.now().plusSeconds(startAfterSeconds)));
    }

    private Runnable wrap(final String key, final Runnable task) {
        return () -> {
            tasks.remove(key);
            task.run();
        };
    }

    public void cancelTask(final String type, final UUID id) {
        final var key = buildKey(type, id);
        final var task = tasks.remove(key);
        if (task != null) {
            task.cancel(false);
        }
    }

    private String buildKey(final String type, final UUID id) {
        return type + '-' + id;
    }

    @Override
    public void close() {
        tasks.clear();
        scheduler.shutdown();
    }
}
