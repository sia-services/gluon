package com.acc.gluon.sql;

import com.acc.gluon.utilities.Container;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SQLManager implements AutoCloseable {
    private final Connection connection;

    public SQLManager(DataSource ds) throws SQLException {
        var connection = ds.getConnection();
        connection.setAutoCommit(false);
        this.connection = connection;
    }

    public TransactionManager transactional() throws SQLException {
        return new TransactionManager(connection);
    }

    public <T> PreparedQuery<T> query(String sql, ResultSetExtractor<T> extractor, StatementPreparator preparator) throws SQLException {
        return new PreparedQuery<>(connection, sql, extractor, preparator);
    }

    public <T> PreparedQuery<T> query(String sql, ResultSetExtractor<T> extractor) throws SQLException {
        return new PreparedQuery<>(connection, sql, extractor, null);
    }

    public PreparedQuery<ResultSet> query(String sql, StatementPreparator preparator) throws SQLException {
        return new PreparedQuery<>(connection, sql, rs -> rs, preparator);
    }

    public PreparedQuery<ResultSet> query(String sql) throws SQLException {
        return new PreparedQuery<>(connection, sql, rs -> rs, null);
    }

    public int sequence(String sequence) throws Exception {
        try (var sequencer = new Sequencer(connection, sequence, 1)) {
            return sequencer.next();
        }
    }

    public String fetchClob(String sql, StatementPreparator preparator) throws SQLException, IOException {
        return fetchClob(sql, preparator, reader -> reader.lines().collect(Collectors.joining("\r\n")));
    }

    public String fetchClob(String sql) throws SQLException, IOException {
        return fetchClob(sql, null);
    }

    public <T> T fetchClob(String sql, StatementPreparator preparator, Function<BufferedReader, T> readerConsumer) throws SQLException, IOException {
        assert readerConsumer != null : "Consumer<BufferedReader> must not be null";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if (preparator != null) preparator.prepare(st);
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    Clob clob = set.getClob(1);
                    if (set.wasNull()) return null;
                    try (BufferedReader bufferedReader = new BufferedReader(clob.getCharacterStream())) {
                        return readerConsumer.apply(bufferedReader);
                    }
                } else {
                    return null;
                }
            }
        }
    }

    public byte[] fetchBlob(String sql, StatementPreparator preparator) throws SQLException, IOException {
        return fetchBlob(sql, preparator, stream -> {
            try {
                return stream.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public byte[] fetchBlob(String sql) throws SQLException, IOException {
        return fetchBlob(sql, null);
    }

    public <T> T fetchBlob(String sql, StatementPreparator preparator, Function<BufferedInputStream, T> streamConsumer) throws SQLException, IOException {
        assert streamConsumer != null : "Consumer<BufferedReader> must not be null";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if (preparator != null) preparator.prepare(st);
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    Blob blob = set.getBlob(1);
                    if (set.wasNull()) return null;
                    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(blob.getBinaryStream())) {
                        return streamConsumer.apply(bufferedInputStream);
                    }
                } else {
                    return null;
                }
            }
        }
    }

    public <Result,PK,Child> List<Result> groupBy(
            String sql,
            Function<ResultSet,PK> by,
            BiFunction<ResultSet,PK, Result> mainCtor,
            Function<ResultSet,Child> childCtor,
            BiConsumer<Result,Child> linker
            ) throws Exception {
        ArrayList<Result> ret = new ArrayList<>(10);

        final Container<PK> currentKey = new Container<>(null);
        final Container<Result> currentValue = new Container<>(null);

        try (var iterable = this.query(sql).fetch()) {
            for (var rs : iterable) {
                var pk = by.apply(rs);

                if (currentValue.getValue() == null) {
                    // first task
                    currentValue.setValue(mainCtor.apply(rs, pk));
                    currentKey.setValue(pk);
                } else if (!currentKey.getValue().equals(pk)) {
                    // current task
                    ret.add(currentValue.getValue());
                    // new task
                    currentValue.setValue(mainCtor.apply(rs, pk));
                    currentKey.setValue(pk);
                }
                var child = childCtor.apply(rs);
                linker.accept(currentValue.getValue(), child);
            }
        }
        if (currentValue.getValue() != null) {
            ret.add(currentValue.getValue());
        }

        return ret;
    }

    public int update(String sql, StatementPreparator preparator) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if (preparator != null) preparator.prepare(st);
            return st.executeUpdate();
        }
    }

    public int update(String sql) throws SQLException {
        return update(sql, null);
    }

    public Updater updater(String sql, StatementPreparator preparator) throws SQLException {
        return new Updater(connection, sql, preparator);
    }

    public Updater updater(String sql) throws SQLException {
        return new Updater(connection, sql, null);
    }

    public BatchUpdater batchUpdater(String sql, StatementPreparator preparator) throws SQLException {
        return new BatchUpdater(connection, sql, preparator);
    }

    public BatchUpdater batchUpdater(String sql) throws SQLException {
        return new BatchUpdater(connection, sql, null);
    }

    public boolean execute(String sql) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            return st.execute();
        }
    }

    public void call(String sql, CallableStatementPreparator preparator) throws SQLException {
        String command = "{call " + sql + "}";
        try (var cstmt = connection.prepareCall(command)) {
            preparator.prepareCall(cstmt);
            cstmt.execute();
        }
    }

    public <V> V call(String sql, CallableStatementExtractor<V> extractor, CallableStatementPreparator preparator) throws SQLException {
        String command = "{call " + sql + "}";
        try (var cstmt = connection.prepareCall(command)) {
            preparator.prepareCall(cstmt);
            var ok = cstmt.execute();
            return extractor.extract(cstmt);
            /*
            execute return true if result is result set; false if result is count
            if (ok) {
                return Optional.of(mapper.mapStatement(cstmt));
            }
            return Optional.empty();
             */
        }
    }

    @Override
    public void close() throws Exception {
        System.out.println("Closing SQLManager");
        connection.close();
    }
}
