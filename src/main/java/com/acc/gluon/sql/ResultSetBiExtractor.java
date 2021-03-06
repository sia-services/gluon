package com.acc.gluon.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetBiExtractor<T,PK> {

    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     * @param rs the ResultSet to map (pre-initialized for the current row)
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     * column values (that is, there's no need to catch SQLException)
     */
    T extract(ResultSet rs, PK pk) throws SQLException;

}
