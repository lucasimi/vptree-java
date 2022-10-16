package org.lucasimi.utils;

public class Ordered<S extends Comparable<S>, T> implements Comparable<Ordered<S, T>> {

    private S order;

    private T data;

    public Ordered(S order, T data) {
        this.order = order;
        this.data = data;
    }

    @Override
    public int compareTo(Ordered<S, T> other) {
        return this.order.compareTo(other.order);
    }

    public S getOrder() {
        return order;
    }

    public void setOrder(S order) {
        this.order = order;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
