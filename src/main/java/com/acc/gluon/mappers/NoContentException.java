package com.acc.gluon.mappers;

import javax.ws.rs.WebApplicationException;

public class NoContentException extends WebApplicationException {
    public NoContentException(String message) {
        super(message);
    }
}
