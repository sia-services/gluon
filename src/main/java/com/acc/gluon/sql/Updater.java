package com.acc.gluon.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Updater implements AutoCloseable {

    protected final PreparedStatement st;
    private final StatementPreparator preparator;

    public Updater(Connection connection, String sql, StatementPreparator preparator) throws SQLException {
        st = connection.prepareStatement(sql);
        this.preparator = preparator;
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
        if (this.preparator != null) this.preparator.prepare(st);
        st.executeUpdate();
    }

}

