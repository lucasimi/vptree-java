package org.lucasimi.vptree;

import java.util.Collection;

public class SplitLeaf<T> implements SplitTree<T> {

    private Collection<T> data;

    public Collection<T> getData() {
        return this.data;
    }

    public SplitLeaf(Collection<T> data) {
        this.data = data;
    }

    @Override
    public void search(SearchAlgorithm<T> searchAlgorithm) {
        searchAlgorithm.search(this);
    }

}
