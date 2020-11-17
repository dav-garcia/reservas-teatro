package com.autentia.tutoriales.reservas.teatro.infra.dispatch.randomasync;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.EventSourceId;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventStream;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RandomAsyncCommandDispatcher implements CommandDispatcher {

    private class AsyncCommandDecorator<T extends AggregateRoot<U>, U> implements Command<T, U> {

        private final Command<T, U> command;

        private AsyncCommandDecorator(Command<T, U> command) {
            this.command = command;
        }

        @Override
        public void execute(U id, Repository<T, U> repository, EventStream<T, U> eventStream) {
            executorService.execute(() -> command.execute(id, repository, eventStream));
        }
    }

    private final CommandDispatcher[] dispatchers;
    private final ExecutorService executorService;
    private final Random random;

    public RandomAsyncCommandDispatcher(final CommandDispatcher... dispatchers) {
        this.dispatchers = dispatchers;
        executorService = buildExecutorService(dispatchers.length);
        random = new Random();
    }

    private ExecutorService buildExecutorService(final int size) {
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
    public <T extends AggregateRoot<U>, U> void registerAggregateRoot(final Class<T> type, final Repository<T, U> repository) {
        Arrays.stream(dispatchers).forEach(d -> d.registerAggregateRoot(type, repository));
    }

    @Override
    public <T extends AggregateRoot<U>, U> void dispatch(final Command<T, U> command, final EventSourceId<T, U> id) {
        dispatchers[random.nextInt(dispatchers.length)].dispatch(new AsyncCommandDecorator<>(command), id);
    }
}
