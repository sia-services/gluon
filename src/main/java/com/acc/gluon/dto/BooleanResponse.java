package com.acc.gluon.dto;

public class BooleanResponse {
    private final static BooleanResponse FALSE = new BooleanResponse(false);
    private final static BooleanResponse TRUE = new BooleanResponse(true);

    public final boolean response;

    private BooleanResponse(boolean response) {
        this.response = response;
    }

    public static BooleanResponse construct(boolean response) {
        return response? TRUE : FALSE;
    }
}
