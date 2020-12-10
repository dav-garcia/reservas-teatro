package com.autentia.tutoriales.reservas.teatro.infra.repository.inmemory;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class DummyPlatformTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() {
        return null;
    }

    @Override
    protected void doBegin(final Object transaction, final TransactionDefinition definition) {
        // Vacío
    }

    @Override
    protected void doCommit(final DefaultTransactionStatus status) {
        // Vacío
    }

    @Override
    protected void doRollback(final DefaultTransactionStatus status) {
        // Vacío
    }
}
