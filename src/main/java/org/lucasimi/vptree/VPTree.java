package org.lucasimi.vptree;

import java.util.Collection;

public interface VPTree<T> {

    public Collection<T> ballSearch(T target, double eps);

    public Collection<T> knnSearch(T target, int neighbors);

}

