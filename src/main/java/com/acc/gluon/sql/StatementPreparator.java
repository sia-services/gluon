package com.acc.gluon.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementPreparator {
    void prepare(PreparedStatement statement) throws SQLException;
}
