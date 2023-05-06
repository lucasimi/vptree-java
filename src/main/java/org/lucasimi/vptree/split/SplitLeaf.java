package org.lucasimi.vptree.split;

import java.util.Collection;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.search.BallSearchResults;
import org.lucasimi.vptree.search.KNNSearchResults;

public class SplitLeaf<T> implements SplitTree<T> {

    private Collection<T> data;

    public Collection<T> getData() {
        return this.data;
    }

    public SplitLeaf(Collection<T> data) {
        this.data = data;
    }

    @Override
    public void ballSearch(BallSearchResults<T> results) {
        Metric<T> metric = results.getMetric();
        T target = results.getTarget();
        double eps = results.getEps();
        for (T x : this.data) {
            if (metric.eval(target, x) <= eps) {
                results.add(x);
            }
        }
    }

    @Override
    public void knnSearch(KNNSearchResults<T> results) {
        results.addAll(this.getData());
    }

}
