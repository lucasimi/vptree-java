package org.lucasimi.vptree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.lucasimi.utils.BinaryTree;
import org.lucasimi.utils.MaxHeap;
import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;

class VPKNNSearch<T> {

    private final T center;

    private final int neighbors;

    private final Metric<T> metric;

    private final MaxHeap<Ordered<Double, T>> points;

    public VPKNNSearch(Metric<T> metric, T center, int neighbors, BinaryTree<VPNode<T>> tree) {
        this.center = center;
        this.neighbors = neighbors;
        this.metric = metric;
        this.points = new MaxHeap<>(neighbors);
        this.search(tree);
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
            return Float.POSITIVE_INFINITY;
        } else {
            return this.points.getMax()
                    .orElseThrow()
                    .getOrder();
        }
    }

    public Set<T> extract() {
        Set<T> collected = new HashSet<>();
        while (!this.points.isEmpty()) {
            this.points.extractMax().map(b -> b.getData()).ifPresent(collected::add);
        }
        return collected;
    }

    private void search(BinaryTree<VPNode<T>> tree) {
        if (tree.isTerminal()) {
            this.addAll(tree.getData().getPoints());
        } else {
            T center = tree.getData().getCenter();
            double radius = tree.getData().getRadius();
            double dist = this.metric.eval(this.center, center);
            double eps = this.getRadius();
            if (dist <= radius + eps) {
                this.search(tree.getLeft());
                eps = this.getRadius();
            }
            if (dist > radius - eps) {
                this.search(tree.getRight());
            }
        }
    }

}
