package com.autentia.tutoriales.reservas.teatro.infra.dispatcher.randomasync;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatcher.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RandomAsyncCommandDispatcher implements CommandDispatcher {

    private final CommandDispatcher[] dispatchers;
    private final ExecutorService executorService;
    private final Random random;

    public RandomAsyncCommandDispatcher(final CommandDispatcher... dispatchers) {
        this.dispatchers = dispatchers;
        executorService = buildExecutorService(dispatchers.length);
        random = new Random();
    }

    private ExecutorService buildExecutorService(int size) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                (runnable, executor) -> {
                    try {
                        executor.getQueue().put(runnable);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
    }

    @Override
    public <T extends AggregateRoot<U>, U> void registerAggregateRoot(Class<T> type, Repository<T, U> repository) {
        Arrays.stream(dispatchers).forEach(d -> d.registerAggregateRoot(type, repository));
    }

    @Override
    public <T extends AggregateRoot<U>, U> void dispatch(Command<T, U> command, EventSourceId<T, U> id) {
        executorService.execute(() -> dispatchers[random.nextInt(dispatchers.length)].dispatch(command, id));
    }
}
