package com.acc.gluon.utilities;

public class ImmutableTriple<L,M,R> {
    public final L left;
    public final M middle;
    public final R right;

    public ImmutableTriple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
}
