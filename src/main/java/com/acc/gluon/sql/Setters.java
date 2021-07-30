package com.acc.gluon.sql;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class Setters {

    public static void setInt(PreparedStatement st, int index, Integer value) throws SQLException {
        if (value != null) {
            st.setInt(index, value);
        } else {
            st.setNull(index, Types.INTEGER);
        }
    }

    public static void setLong(PreparedStatement st, int index, Long value) throws SQLException {
        if (value != null) {
            st.setLong(index, value);
        } else {
            st.setNull(index, Types.INTEGER);
        }
    }

    public static void setBoolean(PreparedStatement st, int index, boolean value) throws SQLException {
        st.setInt(index, value? 1 : 0);
    }

    public static void setBlob(PreparedStatement st, int index, byte[] value) throws SQLException {
        st.setBlob(index, new ByteArrayInputStream(value));
    }


}
