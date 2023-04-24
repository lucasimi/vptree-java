package org.lucasimi.vptree.search;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.split.SplitLeaf;
import org.lucasimi.vptree.split.SplitNode;

public class BallSearch<T> implements SearchAlgorithm<T> {

    private final Metric<T> metric;

    private final T target;

    private final double eps;

    private final List<T> points;

    public BallSearch(Metric<T> metric, T target, double eps) {
        this.metric = metric;
        this.target = target;
        this.eps = eps;
        this.points = new LinkedList<>();
    }

    @Override
    public Collection<T> getPoints() {
        return this.points;
    }

    @Override
    public void search(SplitNode<T> node) {
        T center = node.getCenter();
        double radius = node.getRadius();
        double dist = this.metric.eval(this.target, center);
        if (dist < radius + this.eps) {
            node.getLeft().search(this);
        }
        if (dist >= radius - this.eps) {
            node.getRight().search(this);
        }
    }

    @Override
    public void search(SplitLeaf<T> leaf) {
        for (T x : leaf.getData()) {
            if (this.metric.eval(target, x) <= this.eps) {
                this.points.add(x);
            }
        }
    }

}

