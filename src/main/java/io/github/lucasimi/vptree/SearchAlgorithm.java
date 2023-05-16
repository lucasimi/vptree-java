package io.github.lucasimi.vptree;

import java.util.Collection;

public interface SearchAlgorithm<T> {

    public void search(SplitNode<T> node);

    public void search(SplitLeaf<T> node);

    public Collection<T> getPoints();

}
