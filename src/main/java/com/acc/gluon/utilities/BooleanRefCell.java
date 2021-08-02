package com.acc.gluon.utilities;

public class BooleanRefCell {
    private boolean value;

    public BooleanRefCell(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }
    public void set(boolean value) {
        this.value = value;
    }
}
