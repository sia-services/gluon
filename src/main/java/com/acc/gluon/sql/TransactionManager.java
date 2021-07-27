package com.acc.gluon.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager implements AutoCloseable {
    private final Connection connection;

    public TransactionManager(Connection connection) {
        this.connection = connection;
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws Exception {
        connection.commit();
    }
}
