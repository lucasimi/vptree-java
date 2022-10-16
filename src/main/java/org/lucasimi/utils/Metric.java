package org.lucasimi.utils;

public interface Metric<T> {

    public double eval(T x, T y);

}
