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
        this.leafRadius = builder.getLeafRadius();
        this.leafCapacity = builder.getLeafCapacity();
        this.centers = new ArrayList<>(data.size());
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new Ordered<>(0.0, x));
        }
        this.tree = buildUpd(0, this.dataset.size(), true);
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

    private SplitTree<T> buildLeaf(int start, int bound) {
        List<T> points = new ArrayList<>(bound - start);
        for (int i = start; i < bound; i++) {
            points.add(this.dataset.get(i).getData());
        }
        return new SplitLeaf<>(points);
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

    private void updatePivot(int start, int bound) {
        int pivot = start + RAND.nextInt(bound - start);
        swap(pivot, start);
        T vp = this.dataset.get(start).getData();
        for (int j = start + 1; j < bound; j++) {
            Ordered<Double, T> point = this.dataset.get(j);
            T pointData = point.getData();
            point.setOrder(this.metric.eval(vp, pointData));
        }
    }

    private SplitTree<T> buildUpd(int start, int bound, boolean update) {
        if (bound - start <= this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                this.centers.add(this.dataset.get(i).getData());
            }
            return buildLeaf(start, bound);
        } else {
            int split = (start + bound) / 2;
            if (update) {
                updatePivot(start, bound);
            }
            Pivoter.quickSelect(this.dataset, start + 1, bound, split);
            double radius = this.dataset.get(split).getOrder();
            T vp = this.dataset.get(start).getData();
            SplitTree<T> leftTree;
            SplitTree<T> rightTree;
            if (radius < this.leafRadius) {
                this.centers.add(vp);
                leftTree = buildLeaf(start, split);
            } else {
                leftTree = buildUpd(start, split, false);
            }
            rightTree = buildUpd(split, bound, true);
            return new SplitNode<>(vp, radius, leftTree, rightTree);
        }
    }

    @Override
    public Collection<T> getCenters() {
        return centers;
    }

}
