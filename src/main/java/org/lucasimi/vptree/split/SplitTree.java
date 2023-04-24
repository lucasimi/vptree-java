package org.lucasimi.vptree.split;

import org.lucasimi.vptree.search.SearchAlgorithm;

public interface SplitTree<S> {

    public void search(SearchAlgorithm<S> searchAlgorithm);

}
