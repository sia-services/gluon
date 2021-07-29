package com.acc.gluon.sql;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class Extractors {

    public static Integer getNullableInt(ResultSet rs, int index) throws SQLException {
        var v = rs.getInt(index);
        return rs.wasNull()? null : v;
    }

    public static Long getNullableLong(ResultSet rs, int index) throws SQLException {
        var v = rs.getLong(index);
        return rs.wasNull()? null : v;
    }

    public static Integer getNullableInt(ResultSet rs) throws SQLException {
        return getNullableInt(rs, 1);
    }

    public static Long getNullableLong(ResultSet rs) throws SQLException {
        return getNullableLong(rs, 1);
    }

    public static int getInt(ResultSet rs) throws SQLException {
        return rs.getInt(1);
    }

    public static long getLong(ResultSet rs) throws SQLException {
        return rs.getLong(1);
    }

    public static boolean getBoolean(ResultSet rs, int index) throws SQLException {
        return rs.getInt(index) == 1;
    }

    public static boolean getBoolean(ResultSet rs) throws SQLException {
        return getBoolean(rs, 1);
    }

    public static byte[] getBlob(ResultSet rs, int index) throws SQLException, IOException {
        return getBlob(rs, index, stream -> {
            try {
                return stream.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static <T> T getBlob(ResultSet rs, int index, Function<BufferedInputStream, T> streamConsumer) throws SQLException, IOException {
        Blob blob = rs.getBlob(index);
        if (rs.wasNull()) return null;
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(blob.getBinaryStream())) {
            return streamConsumer.apply(bufferedInputStream);
        }
    }

}
