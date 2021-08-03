package com.acc.gluon.mappers;

import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoContentExceptionMapper implements ExceptionMapper<NoContentException> {

    @Override
    public Response toResponse(NoContentException e) {
        return Response.status(Response.Status.NO_CONTENT).entity(new ExceptionEntity(e)).build();
    }
}
