package com.acc.gluon.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Updater implements AutoCloseable {

    protected final PreparedStatement st;

    public Updater(Connection connection, String sql, StatementPreparator preparator) throws SQLException {
        st = connection.prepareStatement(sql);
        if (preparator != null) preparator.prepare(st);
    }

    @Override
    public void close() throws Exception {
        if (st != null) st.close();
    }

    public void update(StatementPreparator preparator) throws SQLException {
        if (preparator != null) preparator.prepare(st);
        st.executeUpdate();
    }

    public void update() throws SQLException {
        st.executeUpdate();
    }

}

