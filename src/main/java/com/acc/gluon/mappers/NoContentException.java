package com.acc.gluon.mappers;

import javax.ws.rs.WebApplicationException;

public class NoContentException extends WebApplicationException {
    public NoContentException() {
        super("Nu s-a găsit nimic pentru solicitarea dvs.");
    }

    public NoContentException(String message) {
        super(message);
    }
}
