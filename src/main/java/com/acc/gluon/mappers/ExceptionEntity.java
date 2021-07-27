package com.acc.gluon.mappers;


import java.io.Serializable;
import java.util.Optional;

/**
 * Signalises an authentication failure and stores the failure reason.
 *
 * @author Sergio del Amo
 * @since 1.0
 */

public class ExceptionEntity implements Serializable {
    private String message;

    /**
     * @param message The authentication failure description
     */
    public ExceptionEntity(String message) {
        this.message = message;
    }

    public ExceptionEntity(Exception e) {
        this.message = e.getMessage();
    }

    public Optional<String> getMessage() {
        return Optional.of(message);
    }
}
