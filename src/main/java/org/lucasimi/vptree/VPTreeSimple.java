package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.lucasimi.utils.BinaryTree;
import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;

public class VPTreeSimple<T> implements VPTree<T> {

    private Metric<T> metric;

    private BinaryTree<VPNode<T>> tree;

    private int leafCapacity;

    private double leafRadius;

    private List<Ordered<Double, T>> dataset;

    private Collection<T> centers;

    private static final Random rand = new Random();

    public static class Builder<T> {

        private int leafCapacity = 1;

        private double leafRadius = 0.0;

        private boolean randomPivoting = true;

        private Metric<T> metric;

        public Builder<T> withLeafCapacity(int leafCapacity) {
            this.leafCapacity = leafCapacity;
            return this;
        }

        public Builder<T> withLeafRadius(double leafRadius) {
            this.leafRadius = leafRadius;
            return this;
        }

        public Builder<T> withRandomPivoting(boolean randomPivoting) {
            this.randomPivoting = randomPivoting;
            return this;
        }

        public Builder<T> withMetric(Metric<T> metric) {
            this.metric = metric;
            return this;
        }

        public VPTreeSimple<T> build(Collection<T> data) {
            if (this.metric == null) {
                throw new IllegalArgumentException("A metric must be specified");
            }
            return new VPTreeSimple<>(this.metric, this.leafCapacity, this.leafRadius, this.randomPivoting, data);
        }

    }

    private void updateDist(T center, int start, int end) {
        for (int j = start; j < end; j++) {
            Ordered<Double, T> wo = this.dataset.get(j);
            wo.setOrder(this.metric.eval(center, wo.getData()));
        }
    }

    private void swap(int i, int j) {
        if (i == j) {
            return;
        } else {
            Ordered<Double, T> iVal = this.dataset.get(i);
            Ordered<Double, T> jVal = this.dataset.get(j);
            this.dataset.set(i, jVal);
            this.dataset.set(j, iVal);
        }
    }

    public Collection<T> getCenters() {
        return this.centers;
    }

    private VPTreeSimple(Metric<T> metric, int capacity, double radius, boolean randomPivot, Collection<T> data) {
        this.metric = metric;
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new Ordered<>(0.0, x));
        }
        this.leafRadius = radius;
        this.leafCapacity = capacity;
        this.centers = new ArrayList<>(data.size());
        if (randomPivot) {
            this.tree = build(0, this.dataset.size());
        } else {
            this.tree = buildUpdate(0, this.dataset.size());
        }
    }

    private BinaryTree<VPNode<T>> buildLeaf(int start, int end) {
        List<T> points = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            points.add(this.dataset.get(i).getData());
        }
        return new BinaryTree<>(new VPNodeLeaf<>(points));
    }

    private BinaryTree<VPNode<T>> build(int start, int end) {
        if (end - start <= this.leafCapacity) {
            for (int i = start; i < end; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, end);
        } else {
            int mid = (start + end) / 2;
            int pivot = start + rand.nextInt(end - start);
            swap(pivot, start);
            Ordered<Double, T> pivotPoint = this.dataset.get(start);
            T vantagePoint = pivotPoint.getData();
            updateDist(vantagePoint, start + 1, end);
            Pivoter.quickSelect(this.dataset, start + 1, end, mid);
            Ordered<Double, T> furthestPoint = this.dataset.get(mid);
            double radius = furthestPoint.getOrder();
            BinaryTree<VPNode<T>> leftTree;
            BinaryTree<VPNode<T>> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vantagePoint);
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = build(start, mid);
            }
            rightTree = build(mid, end);
            VPNode<T> container = new VPNodeSplit<>(vantagePoint, radius);
            return new BinaryTree<>(container, leftTree, rightTree);
        }
    }

    private BinaryTree<VPNode<T>> buildUpdate(int start, int end) {
        if (end - start <= this.leafCapacity) {
            for (int i = start; i < end; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, end);
        } else {
            int mid = (start + end) / 2;
            Ordered<Double, T> center = this.dataset.get(start);
            updateDist(center.getData(), start + 1, end);
            Pivoter.quickSelect(this.dataset, start + 1, end, mid);
            Ordered<Double, T> furthest = this.dataset.get(mid);
            double radius = furthest.getOrder();
            BinaryTree<VPNode<T>> leftTree;
            BinaryTree<VPNode<T>> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdate(start, mid);
            }
            rightTree = buildUpdate(mid, end);
            VPNode<T> container = new VPNodeSplit<>(center.getData(), radius);
            return new BinaryTree<>(container, leftTree, rightTree);
        }
    }

    private BinaryTree<VPNode<T>> buildNoUpdate(int start, int end) {
        if (end - start <= this.leafCapacity) {
            for (int i = start; i < end; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, end);
        } else {
            int mid = (start + end) / 2;
            Ordered<Double, T> center = this.dataset.get(start);
            Pivoter.quickSelect(this.dataset, start + 1, end, mid);
            Ordered<Double, T> furthest = this.dataset.get(mid);
            double radius = furthest.getOrder();
            BinaryTree<VPNode<T>> leftTree;
            BinaryTree<VPNode<T>> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdate(start, mid);
            }
            rightTree = buildUpdate(mid, end);
            VPNode<T> container = new VPNodeSplit<>(center.getData(), radius);
            return new BinaryTree<>(container, leftTree, rightTree);
        }
    }

    @Override
    public Collection<T> ballSearch(T testPoint, double eps) {
        VPBallSearch<T> search = new VPBallSearch<T>(this.metric, testPoint, eps, this.tree);
        return search.getPoints();
    }

    @Override
    public Collection<T> knnSearch(T testPoint, int k) {
        VPKNNSearch<T> search = new VPKNNSearch<T>(this.metric, testPoint, k, this.tree);
        return search.extract();
    }

}
