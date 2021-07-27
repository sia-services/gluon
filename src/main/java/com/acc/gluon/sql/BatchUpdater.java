package com.acc.gluon.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BatchUpdater implements AutoCloseable {

    protected final PreparedStatement st;
    private final Connection connection;

    public BatchUpdater(Connection connection, String sql, StatementPreparator preparator) throws SQLException {
        this.connection = connection;
        st = connection.prepareStatement(sql);
        if (preparator != null) preparator.prepare(st);
    }

    public BatchUpdater(Connection connection, String sql) throws SQLException {
        this.connection = connection;
        st = connection.prepareStatement(sql);
    }

    @Override
    public void close() throws Exception {
        st.executeBatch();
        st.close();
    }

    public void update(StatementPreparator preparator) throws SQLException {
        if (preparator != null) preparator.prepare(st);
        st.addBatch();
    }

    public void update() throws SQLException {
        st.addBatch();
    }

    public void commit() throws SQLException {
        st.executeBatch();
    }
}
