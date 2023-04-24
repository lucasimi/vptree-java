package org.lucasimi.vptree.search;

import java.util.Collection;

import org.lucasimi.vptree.split.SplitNode;
import org.lucasimi.vptree.split.SplitLeaf;

public interface SearchAlgorithm<T> {

    public void search(SplitNode<T> node);

    public void search(SplitLeaf<T> node);

    public Collection<T> getPoints();

}
