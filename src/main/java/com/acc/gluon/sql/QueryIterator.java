package com.acc.gluon.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class QueryIterator<T>  implements Iterator<T>, AutoCloseable {

    private final ResultSet rs;
    private final ResultSetExtractor<T> extractor;

    protected QueryIterator(ResultSet rs, ResultSetExtractor<T> extractor) {
        this.rs = rs;
        this.extractor = extractor;
    }

    @Override
    public boolean hasNext() {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T next() {
        try {
            return extractor.extract(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        rs.close();
    }
}
