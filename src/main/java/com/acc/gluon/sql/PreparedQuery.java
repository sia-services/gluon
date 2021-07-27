package com.acc.gluon.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PreparedQuery<T> implements AutoCloseable {
    protected final PreparedStatement st;
    protected final ResultSetExtractor<T> extractor;
    protected final StatementPreparator preparator;

    public PreparedQuery(Connection connection, String sql, ResultSetExtractor<T> extractor, StatementPreparator preparator) throws SQLException {
        assert sql != null : "SQL must not be null";
        assert extractor != null : "ResultSetExtractor must not be null";
        st = connection.prepareStatement(sql);

        this.extractor = extractor;

        st.setFetchSize(100);
        this.preparator = preparator;
    }

    @Override
    public void close() throws Exception {
        if (st != null) st.close();
    }

    public List<T> fetchList() throws SQLException {
        if (preparator != null) preparator.prepare(st);
        try (ResultSet rs = st.executeQuery()) {
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractor.extract(rs));
            }
            return list;
        }
    }

    public Optional<T> fetchOne() throws SQLException {
        if (preparator != null) preparator.prepare(st);
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return Optional.of(extractor.extract(rs));
            } else {
                return Optional.empty();
            }
        }
    }

    public CloseableIterable<T> fetch() throws SQLException {
        if (preparator != null) preparator.prepare(st);
        ResultSet rs = st.executeQuery();

        final var closeableSt = st;
        return new CloseableIterable<>() {
            QueryIterator<T> iterator = new QueryIterator<>(rs, extractor);

            @Override
            public void close() throws Exception {
                iterator.close();
                closeableSt.close();
            }

            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

}
