package org.lucasimi.vptree;

import java.util.Collection;

public interface VPTree<T> {

    public Collection<T> ballSearch(T testPoint, double eps);

    public Collection<T> knnSearch(T testPoint, int k);

}
