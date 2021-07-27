package com.acc.gluon.utilities;

public class ImmutablePair<F,S> {
    public final F left;
    public final S right;

    public ImmutablePair(F left, S right) {
        this.left = left;
        this.right = right;
    }
}
