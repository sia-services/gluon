package com.acc.gluon.utilities;

public class RefCell<T> {
    private T value;

    public RefCell(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
