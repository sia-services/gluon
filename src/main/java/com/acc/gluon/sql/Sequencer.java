package com.acc.gluon.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sequencer implements AutoCloseable {
    private final PreparedStatement st;
    private int cacheCount = 1;

    public Sequencer(Connection connection, String sequence, int cacheCount) throws SQLException {
        assert sequence != null : "Secuence name must not be null";
        this.st = connection.prepareStatement("select " + sequence + ".nextval from dual");
        this.cacheCount = cacheCount;
        // TODO: cache sequence numbers
    }

    @Override
    public void close() throws Exception {
        if (st != null) st.close();
    }

    public int next() throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    public long nextLong() throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                return 0;
            }
        }
    }
}
