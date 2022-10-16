package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.lucasimi.utils.BinaryTree;
import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;

public class VPTree<T> {

    private Metric<T> metric;

    private BinaryTree<VPNode<T>> tree;

    private int leafCapacity;

    private double leafRadius;

    private List<Ordered<Double, T>> dataset;

    private Collection<T> centers;

    private static final Random rand = new Random();

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

    public VPTree(Metric<T> metric, Collection<T> data) {
        this(metric, data, 1, 0.0);
    }

    public VPTree(Metric<T> metric, Collection<T> data, int leafCapacity) {
        this(metric, data, leafCapacity, 0.0);
    }

    public VPTree(Metric<T> metric, Collection<T> data, int leafSize, Double leafRadius) {
        this.metric = metric;
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new Ordered<>(0.0, x));
        }
        this.leafRadius = leafRadius;
        this.leafCapacity = leafSize;
        this.centers = new ArrayList<>(data.size());
        this.tree = build(0, this.dataset.size());
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

    public List<T> ballSearch(T testPoint, double eps) {
        VPBallSearch<T> search = new VPBallSearch<T>(this.metric, testPoint, eps, this.tree);
        return search.getPoints();
    }

    public Set<T> knnSearch(T testPoint, int k) {
        VPKNNSearch<T> search = new VPKNNSearch<T>(this.metric, testPoint, k, this.tree);
        return search.extract();
    }

}
