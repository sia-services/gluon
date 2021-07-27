package com.acc.gluon.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.sql.SQLException;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

    @Override
    public Response toResponse(SQLException throwables) {
        throwables.printStackTrace();
        return Response.serverError().build();
    }

}
