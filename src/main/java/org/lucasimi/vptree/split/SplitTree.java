package org.lucasimi.vptree.split;

import org.lucasimi.vptree.search.BallSearchResults;
import org.lucasimi.vptree.search.KNNSearchResults;

public interface SplitTree<S> {

    public void ballSearch(BallSearchResults<S> results);

    public void knnSearch(KNNSearchResults<S> results);

}
