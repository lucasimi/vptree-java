package org.lucasimi.vptree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lucasimi.utils.BinaryTree;
import org.lucasimi.utils.Metric;

class VPBallSearch<T> {

    private final T center;

    private final double eps;

    private final List<T> points;

    private final Metric<T> metric;

    public VPBallSearch(Metric<T> metric, T center, double eps, BinaryTree<VPNode<T>> tree) {
        this.center = center;
        this.eps = eps;
        this.metric = metric;
        this.points = new LinkedList<>();
        this.search(tree);
    }

    private void addAll(Collection<T> points) {
        for (T point : points) {
            if (this.metric.eval(this.center, point) <= this.eps) {
                this.points.add(point);
            }
        }
    }

    public List<T> getPoints() {
        return this.points;
    }

    private void search(BinaryTree<VPNode<T>> tree) {
        if (tree.isTerminal()) {
            this.addAll(tree.getData().getPoints());
        } else {
            T center = tree.getData().getCenter();
            double radius = tree.getData().getRadius();
            double dist = this.metric.eval(this.center, center);
            if (dist < radius - this.eps) {
                this.search(tree.getLeft());
            } else if (dist >= radius + this.eps) {
                this.search(tree.getRight());
            } else {
                this.search(tree.getLeft());
                this.search(tree.getRight());
            }
        }
    }

}
