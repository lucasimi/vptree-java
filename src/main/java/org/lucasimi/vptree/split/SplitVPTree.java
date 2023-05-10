package org.lucasimi.vptree.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;
import org.lucasimi.vptree.VPTree;
import org.lucasimi.vptree.search.BallSearchResults;
import org.lucasimi.vptree.search.KNNSearchResults;

public class SplitVPTree<T> implements VPTree<T> {

    private static final Random RAND = new Random();

    private final Metric<T> metric;

    private final SplitTree<T> tree;

    private final int leafCapacity;

    private final double leafRadius;

    private final List<Ordered<Double, T>> dataset;

    private final Collection<T> centers;

    public SplitVPTree(Builder<T> builder, Collection<T> data) {
        this.metric = builder.getMetric();
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new Ordered<>(0.0, x));
        }
        this.leafRadius = builder.getLeafRadius();
        this.leafCapacity = builder.getLeafCapacity();
        this.centers = new ArrayList<>(data.size());
        if (builder.isRandomPivoting()) {
            this.tree = buildRandRec(0, this.dataset.size());
        } else {
            this.tree = buildUpdateRec(0, this.dataset.size());
        }
    }

    @Override
    public Collection<T> ballSearch(T target, double eps) {
        BallSearchResults<T> ballSearch = new BallSearchResults<>(this.metric, target, eps);
        this.tree.ballSearch(ballSearch);
        return ballSearch.extractPoints();
    }

    @Override
    public Collection<T> knnSearch(T target, int neighbors) {
        KNNSearchResults<T> results = new KNNSearchResults<>(this.metric, target, neighbors);
        this.tree.knnSearch(results);
        return results.extractPoints();
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

    private static int getMid(int start, int bound) {
        return (start + bound) / 2;
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
            int mid = getMid(start, bound);
            int pivot = start + RAND.nextInt(bound - start);
            swap(pivot, start);
            Ordered<Double, T> vpOrd = this.dataset.get(start);
            T vp = vpOrd.getData();
            updateDist(vp, start + 1, bound);
            double radius = process(start, bound, mid);
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vp);
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildRandRec(start, mid);
            }
            rightTree = buildRandRec(mid, bound);
            return new SplitNode<>(vp, radius, leftTree, rightTree);
        }
    }

    private SplitTree<T> buildUpdateRec(int start, int bound) {
        if (bound - start <= this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, bound);
        } else {
            int mid = getMid(start, bound);
            Ordered<Double, T> vpOrd = this.dataset.get(start);
            T vp = vpOrd.getData();
            updateDist(vp, start + 1, bound);
            double radius = process(start, bound, mid);
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vpOrd.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdateRec(start, mid);
            }
            rightTree = buildUpdateRec(mid, bound);
            return new SplitNode<>(vpOrd.getData(), radius, leftTree, rightTree);
        }
    }

    private SplitTree<T> buildNoUpdateRec(int start, int bound) {
        if (bound - start <= this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, bound);
        } else {
            int mid = getMid(start, bound);
            Ordered<Double, T> vpOrd = this.dataset.get(start);
            double radius = process(start, bound, mid);
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vpOrd.getData());
                leftTree = buildLeaf(start, mid);
            } else {
                leftTree = buildNoUpdateRec(start, mid);
            }
            rightTree = buildUpdateRec(mid, bound);
            return new SplitNode<>(vpOrd.getData(), radius, leftTree, rightTree);
        }
    }

    private void updateDist(T center, int start, int bound) {
        for (int j = start; j < bound; j++) {
            Ordered<Double, T> wo = this.dataset.get(j);
            wo.setOrder(this.metric.eval(center, wo.getData()));
        }
    }

    private double process(int start, int bound, int mid) {
        Pivoter.quickSelect(this.dataset, start + 1, bound, mid);
        Ordered<Double, T> furthest = this.dataset.get(mid);
        double radius = furthest.getOrder();
        return radius;
    }

    @Override
    public Collection<T> getCenters() {
        return centers;
    }

}
