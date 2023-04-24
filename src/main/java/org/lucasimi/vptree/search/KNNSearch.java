package org.lucasimi.vptree.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.lucasimi.utils.MaxHeap;
import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.vptree.split.SplitLeaf;
import org.lucasimi.vptree.split.SplitNode;

public class KNNSearch<T> implements SearchAlgorithm<T> {

    private final Metric<T> metric;

    private final T center;

    private final int neighbors;

    private final MaxHeap<Ordered<Double, T>> points;

    public KNNSearch(Metric<T> metric, T center, int neighbors) {
        this.metric = metric;
        this.center = center;
        this.neighbors = neighbors;
        this.points = new MaxHeap<>(neighbors);
    }

    public void add(T data) {
        double dist = this.metric.eval(this.center, data);
        double radius = this.getRadius();
        if (dist <= radius) {
            this.points.add(new Ordered<>(dist, data));
            while (this.points.size() > neighbors) {
                this.points.extractMax();
            }
        }
    }

    public void addAll(Collection<T> data) {
        data.stream().forEach(this::add);
    }

    public double getRadius() {
        if (this.points.size() < this.neighbors) {
            return Double.POSITIVE_INFINITY;
        } else {
            return this.points.getMax()
                    .orElseThrow()
                    .getOrder();
        }
    }

    @Override
    public Collection<T> getPoints() {
        Set<T> collected = new HashSet<>();
        while (!this.points.isEmpty()) {
            this.points.extractMax()
                    .map(b -> b.getData())
                    .ifPresent(collected::add);
        }
        return collected;
    }

    @Override
    public void search(SplitLeaf<T> leaf) {
        this.addAll(leaf.getData());
    }

    @Override
    public void search(SplitNode<T> node) {
        double radius = node.getRadius();
        double dist = this.metric.eval(this.center, node.getCenter());
        double eps = this.getRadius();
        if (dist < radius + eps) {
            node.getLeft().search(this);
            eps = this.getRadius();
        }
        if (dist >= radius - eps) {
            node.getRight().search(this);
        }
    }

}

