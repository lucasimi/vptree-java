package org.lucasimi.vptree.split;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.search.BallSearchResults;
import org.lucasimi.vptree.search.KNNSearchResults;

public class SplitNode<T> implements SplitTree<T> {

    private final T center;

    private final double radius;

    private final SplitTree<T> left;

    private final SplitTree<T> right;

    public SplitNode(T center, double radius, SplitTree<T> left, SplitTree<T> right) {
        this.center = center;
        this.radius = radius;
        this.left = left;
        this.right = right;
    }
    
    @Override
    public void ballSearch(BallSearchResults<T> results) {
        double dist = results.getMetric().eval(results.getTarget(), this.center);
        if (dist < radius + results.getEps()) {
            this.getLeft().ballSearch(results);
        }
        if (dist >= radius - results.getEps()) {
            this.getRight().ballSearch(results);
        }
    }

    @Override
    public void knnSearch(KNNSearchResults<T> results) {
        Metric<T> metric = results.getMetric();
        T knnCenter = results.getTarget();
        double dist = metric.eval(knnCenter, this.center);
        double eps = results.getRadius();
        if (dist < radius + eps) {
            this.getLeft().knnSearch(results);
            eps = results.getRadius();
        }
        if (dist >= radius - eps) {
            this.getRight().knnSearch(results);
        }
    }

    public T getCenter() {
        return this.center;
    }

    public double getRadius() {
        return this.radius;
    }

    public SplitTree<T> getLeft() {
        return this.left;
    }

    public SplitTree<T> getRight() {
        return this.right;
    }

}
