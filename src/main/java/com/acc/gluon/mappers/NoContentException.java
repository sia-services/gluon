package com.acc.gluon.mappers;

import javax.ws.rs.WebApplicationException;

public class NoContentException extends WebApplicationException {
    private static final long serialVersionUID = 5541728429461603695L;

    public NoContentException() {
        super("Nu s-a găsit nimic pentru solicitarea dvs.");
    }

    public NoContentException(String message) {
        super(message);
    }
}
