package com.acc.gluon.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException ex) {
        // ex.printStackTrace();
        return Response.status(Response.Status.BAD_REQUEST).entity(new ExceptionEntity(ex)).build();
    }

}
