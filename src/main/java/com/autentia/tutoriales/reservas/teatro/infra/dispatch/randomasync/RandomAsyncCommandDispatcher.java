package com.autentia.tutoriales.reservas.teatro.infra.dispatch.randomasync;

import com.autentia.tutoriales.reservas.teatro.infra.AggregateRoot;
import com.autentia.tutoriales.reservas.teatro.infra.Command;
import com.autentia.tutoriales.reservas.teatro.infra.dispatch.CommandDispatcher;
import com.autentia.tutoriales.reservas.teatro.infra.repository.Repository;
import com.autentia.tutoriales.reservas.teatro.infra.stream.EventPublisher;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RandomAsyncCommandDispatcher<T extends AggregateRoot<U>, U> implements CommandDispatcher<T, U> {

    private class AsyncCommandDecorator implements Command<T, U> {

        private final Command<T, U> command;

        private AsyncCommandDecorator(final Command<T, U> command) {
            this.command = command;
        }

        @Override
        public void execute(final U id, final Repository<T, U> repository, final EventPublisher<U> eventPublisher) {
            executorService.execute(() -> command.execute(id, repository, eventPublisher));
        }
    }

    private final CommandDispatcher<T, U>[] dispatchers;
    private final ExecutorService executorService;
    private final Random random;

    public RandomAsyncCommandDispatcher(final CommandDispatcher<T, U>... dispatchers) {
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
    public void dispatch(final U id, final Command<T, U> command) {
        dispatchers[random.nextInt(dispatchers.length)].dispatch(id, new AsyncCommandDecorator(command));
    }
}
