package org.lucasimi.vptree;

import java.util.Collection;

public interface VPNode<T> {

    public double getRadius();

    public T getCenter();

    public Collection<T> getPoints();

}
