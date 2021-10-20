package com.acc.gluon.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class PreparedCall<V> implements AutoCloseable {
    private final CallableStatement st;
    private final CallableStatementExtractor<V> extractor;
    private final CallableStatementPreparator preparator;

    public PreparedCall(Connection connection, String sql, CallableStatementExtractor<V> extractor, CallableStatementPreparator preparator) throws SQLException {
        String command = "{call " + sql + "}";
        this.st = connection.prepareCall(command);
        this.extractor = extractor;
        this.preparator = preparator;
    }

    @Override
    public void close() throws Exception {
        if (st != null) st.close();
    }

    public V call() throws SQLException {
        return this.call(null);
    }

    public V call(CallableStatementPreparator preparator) throws SQLException {
        if (this.preparator != null) this.preparator.prepareCall(st);
        if (preparator != null) preparator.prepareCall(st);
        var ok = st.execute();
        return extractor.extract(st);
    }
}
