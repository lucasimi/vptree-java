package org.lucasimi.vptree.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;
import org.lucasimi.vptree.VPTree;
import org.lucasimi.vptree.search.BallSearch;
import org.lucasimi.vptree.search.KNNSearch;

public class SplitVPTree<T> implements VPTree<T> {

    private static final Random RAND = new Random();

    private final Metric<T> metric;

    private final SplitTree<T> tree;

    private final int leafCapacity;

    private final double leafRadius;

    private final List<Ordered<Double, T>> dataset;

    private final Collection<T> centers;

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    private SplitVPTree(Builder<T> builder, Collection<T> data) {
        this.metric = builder.metric;
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new Ordered<>(0.0, x));
        }
        this.leafRadius = builder.leafRadius;
        this.leafCapacity = builder.leafCapacity;
        this.centers = new ArrayList<>(data.size());
        if (builder.randomPivoting) {
            this.tree = buildRandRec(0, this.dataset.size());
        } else {
            this.tree = buildUpdateRec(0, this.dataset.size());
        }
    }

    @Override
    public Collection<T> ballSearch(T target, double eps) {
        BallSearch<T> ballSearch = new BallSearch<>(this.metric, target, eps);
        this.tree.search(ballSearch);
        return ballSearch.getPoints();
    }

    @Override
    public Collection<T> knnSearch(T target, int neighbors) {
        KNNSearch<T> knnSearch = new KNNSearch<>(this.metric, target, neighbors);
        this.tree.search(knnSearch);
        return knnSearch.getPoints();
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

    private SplitTree<T> buildLeaf(int start, int bound) {
        List<T> points = new ArrayList<>(bound - start);
        for (int i = start; i < bound; i++) {
            points.add(this.dataset.get(i).getData());
        }
        return new SplitLeaf<>(points);
    }

    private SplitTree<T> buildRandRec(int start, int bound) {
        if (bound - start <= this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, bound);
        } else {
            int mid = (start + bound) / 2;
            int pivot = start + RAND.nextInt(bound);
            swap(pivot, start);
            Ordered<Double, T> pivotPoint = this.dataset.get(start);
            T vantagePoint = pivotPoint.getData();
            updateDist(vantagePoint, start + 1, bound);
            Pivoter.quickSelect(this.dataset, start + 1, bound, mid);
            Ordered<Double, T> furthestPoint = this.dataset.get(mid);
            double radius = furthestPoint.getOrder();
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vantagePoint);
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildRandRec(start, mid);
            }
            rightTree = buildRandRec(mid, bound);
            return new SplitNode<>(vantagePoint, radius, leftTree, rightTree);
        }
    }

    private SplitTree<T> buildUpdateRec(int start, int bound) {
        if (bound - start <= this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, bound);
        } else {
            int mid = (start + bound) / 2;
            Ordered<Double, T> center = this.dataset.get(start);
            updateDist(center.getData(), start + 1, bound);
            Pivoter.quickSelect(this.dataset, start + 1, bound, mid);
            Ordered<Double, T> furthest = this.dataset.get(mid);
            double radius = furthest.getOrder();
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdateRec(start, mid);
            }
            rightTree = buildUpdateRec(mid, bound);
            return new SplitNode<>(center.getData(), radius, leftTree, rightTree);
        }
    }

    private SplitTree<T> buildNoUpdateRec(int start, int bound) {
        if (bound - start <= this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, bound);
        } else {
            int mid = (start + bound) / 2;
            Ordered<Double, T> center = this.dataset.get(start);
            Pivoter.quickSelect(this.dataset, start + 1, bound, mid);
            Ordered<Double, T> furthest = this.dataset.get(mid);
            double radius = furthest.getOrder();
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdateRec(start, mid);
            }
            rightTree = buildUpdateRec(mid, bound);
            return new SplitNode<>(center.getData(), radius, leftTree, rightTree);
        }
    }

    private void updateDist(T center, int start, int bound) {
        for (int j = start; j < bound; j++) {
            Ordered<Double, T> wo = this.dataset.get(j);
            wo.setOrder(this.metric.eval(center, wo.getData()));
        }
    }

    public Collection<T> getCenters() {
        return centers;
    }
    
    public static class Builder<T> {

        private int leafCapacity = 1;

        private double leafRadius = 0.0;

        private boolean randomPivoting = true;

        private Metric<T> metric;

        private Builder() {}

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

        public SplitVPTree<T> build(Collection<T> data) {
            return new SplitVPTree<>(this, data);
        }

    }

}
