package io.github.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.github.lucasimi.utils.MaxHeap;
import io.github.lucasimi.utils.Metric;
import io.github.lucasimi.utils.Ordered;
import io.github.lucasimi.utils.Pivoter;

public class VPTree<T> {

    private final Metric<T> metric;

    private final SplitTree<T> tree;

    private final int leafCapacity;

    private final double leafRadius;

    private final List<Ordered<Double, T>> dataset;

    private final Collection<T> centers;

    private static final Random rand = new Random();

    public class BallSearch implements SearchAlgorithm<T> {

        private final T target;

        private final double eps;

        private final List<T> points;

        public BallSearch(T target, double eps) {
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
            double dist = VPTree.this.metric.eval(this.target, center);
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
                if (VPTree.this.metric.eval(target, x) <= this.eps) {
                    this.points.add(x);
                }
            }
        }

    }

    public class KNNSearch implements SearchAlgorithm<T> {

        private final T center;

        private final int neighbors;

        private final MaxHeap<Ordered<Double, T>> points;

        public KNNSearch(T center, int neighbors) {
            this.center = center;
            this.neighbors = neighbors;
            this.points = new MaxHeap<>(neighbors);
        }

        public void add(T data) {
            double dist = VPTree.this.metric.eval(this.center, data);
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
            double dist = VPTree.this.metric.eval(this.center, node.getCenter());
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

        public VPTree<T> build(Collection<T> data) {
            if (this.metric == null) {
                throw new IllegalArgumentException("A metric must be specified");
            }
            return new VPTree<>(this.metric, this.leafCapacity, this.leafRadius, this.randomPivoting, data);
        }

    }

    private VPTree(Metric<T> metric, int capacity, double radius, boolean randomPivot, Collection<T> data) {
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

    private SplitTree<T> buildLeaf(int start, int end) {
        List<T> points = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            points.add(this.dataset.get(i).getData());
        }
        return new SplitLeaf<>(points);
    }

    private SplitTree<T> build(int start, int end) {
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
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vantagePoint);
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = build(start, mid);
            }
            rightTree = build(mid, end);
            return new SplitNode<>(vantagePoint, radius, leftTree, rightTree);
        }
    }

    private SplitTree<T> buildUpdate(int start, int end) {
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
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdate(start, mid);
            }
            rightTree = buildUpdate(mid, end);
            return new SplitNode<>(center.getData(), radius, leftTree, rightTree);
        }
    }

    private SplitTree<T> buildNoUpdate(int start, int end) {
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
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(center.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdate(start, mid);
            }
            rightTree = buildUpdate(mid, end);
            return new SplitNode<>(center.getData(), radius, leftTree, rightTree);
        }
    }

    private void updateDist(T center, int start, int end) {
        for (int j = start; j < end; j++) {
            Ordered<Double, T> wo = this.dataset.get(j);
            wo.setOrder(this.metric.eval(center, wo.getData()));
        }
    }

    public Collection<T> getCenters() {
        return centers;
    }

    public Collection<T> ballSearch(T target, double eps) {
        BallSearch ballSearch = new BallSearch(target, eps);
        this.tree.search(ballSearch);
        return ballSearch.getPoints();
    }

    public Collection<T> knnSearch(T target, int neighbors) {
        KNNSearch knnSearch = new KNNSearch(target, neighbors);
        this.tree.search(knnSearch);
        return knnSearch.getPoints();
    }

}
