package com.acc.gluon.sql;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface CallableStatementPreparator {
    void prepareCall(CallableStatement statement) throws SQLException;
}
