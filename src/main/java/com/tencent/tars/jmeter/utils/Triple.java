package com.tencent.tars.jmeter.utils;

import java.io.Serializable;
import java.util.Objects;

public abstract class Triple<L, M, R> implements Serializable {


    public abstract L getLeft();

    public abstract M getMiddle();

    public abstract R getRight();

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triple<?, ?, ?>) {
            final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
            return Objects.equals(getLeft(), other.getLeft())
                    && Objects.equals(getMiddle(), other.getMiddle())
                    && Objects.equals(getRight(), other.getRight());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getLeft() == null ? 0 : getLeft().hashCode()) ^
                (getMiddle() == null ? 0 : getMiddle().hashCode()) ^
                (getRight() == null ? 0 : getRight().hashCode());
    }

    @Override
    public String toString() {
        return "(" + getLeft() + "," + getMiddle() + "," + getRight() + ")";
    }

}