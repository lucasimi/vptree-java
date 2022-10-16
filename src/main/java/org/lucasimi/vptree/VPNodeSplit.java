package org.lucasimi.vptree;

import java.util.Collection;
import java.util.Collections;

class VPNodeSplit<T> implements VPNode<T> {

    private T center;

    private double radius;

    public VPNodeSplit(T center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public T getCenter() {
        return this.center;
    }

    @Override
    public Collection<T> getPoints() {
        return Collections.emptyList();
    }

}
