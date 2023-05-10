package org.lucasimi.vptree.flat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;
import org.lucasimi.vptree.VPTree;
import org.lucasimi.vptree.search.BallSearchResults;

public class FlatVPTree<T> implements VPTree<T> {

    private static final Random RAND = new Random();

    private final int leafCapacity;

    private final double leafRadius;

    private final Metric<T> metric;

    private final ArrayList<Ordered<Double, T>> vpArr;

    public FlatVPTree(Builder<T> builder, Collection<T> data) {
        this.metric = builder.getMetric();
        this.vpArr = new ArrayList<>(data.size());
        this.leafCapacity = builder.getLeafCapacity();
        this.leafRadius = builder.getLeafRadius();
        for (T x : data) {
            this.vpArr.add(new Ordered<>(0.0, x));
        }
        if (builder.isRandomPivoting()) {
            buildRand();
        } else {
            buildNoRand();
        }
    }

	@Override
    public Collection<T> ballSearch(T target, double eps) {
        BallSearchResults<T> results = new BallSearchResults<>(this.metric, target, eps);
        ballSearchRec(results, 0, this.vpArr.size()); 
        return results.extractPoints(); 
    }

	@Override
	public Collection<T> knnSearch(T target, int neighbors) {
		return Collections.emptyList();
	}

    private void swap(int i, int j) {
        if (i == j) {
            return;
        } else {
            Ordered<Double, T> iVal = this.vpArr.get(i);
            Ordered<Double, T> jVal = this.vpArr.get(j);
            this.vpArr.set(i, jVal);
            this.vpArr.set(j, iVal);
        }
    }

    private void buildRand() {
        buildRandRec(0, this.vpArr.size());
    }

    private void buildNoRand() {
        buildNoRandRec(0, this.vpArr.size());
    }

    private void updateDist(T center, int start, int bound) {
        for (int i = start; i < bound; i++) {
            Ordered<Double, T> point = this.vpArr.get(i);
            point.setOrder(this.metric.eval(center, point.getData()));
        }
    }

    private void buildNoRandRec(int start, int bound) {
        if (bound <= start + this.leafCapacity) {
            return;
        }
        int mid = getMid(start, bound);
        double radius = processVpArr(start, bound, mid);
        if (radius >= this.leafRadius) {
            buildNoRandRec(start + 1, mid);
        }
        buildNoRandRec(mid, bound);
    }

    private static int getMid(int start, int bound) {
        return (start + 1 + bound) / 2;
    }

    private double processVpArr(int start, int bound, int mid) {
        Ordered<Double, T> center = this.vpArr.get(start);
        updateDist(center.getData(), start, bound);
        Pivoter.quickSelect(this.vpArr, start + 1, bound, mid);
        double radius = this.vpArr.get(mid).getOrder();
        this.vpArr.get(start).setOrder(radius);
        return radius;
    }

    private void buildRandRec(int start, int bound) {
        if (bound <= start + this.leafCapacity) {
            return;
        }
        int pivot = start + RAND.nextInt(bound - start);
        swap(start, pivot);
        int mid = getMid(start, bound);
        double radius = processVpArr(start, bound, mid);
        if (radius >= this.leafRadius) {
            buildRandRec(start + 1, mid);
        }
        buildRandRec(mid, bound);
    }

    private void ballSearchRec(BallSearchResults<T> results, int start, int bound) {
        double eps = results.getEps();
        T target = results.getTarget();
        if (bound <= start + this.leafCapacity) {
            for (int i = start; i < bound; i++) {
                Ordered<Double, T> ord = this.vpArr.get(i);
                T center = ord.getData();
                double dist = this.metric.eval(center, target);
                if (dist <= eps) {
                    results.add(center);
                }
            }
        } else {
            int mid = getMid(start, bound);
            Ordered<Double, T> ord = this.vpArr.get(start);
            T center = ord.getData();
            double radius = ord.getOrder();
            if (radius < this.leafRadius) {
                for (int i = start; i < bound; i++) {
                    T point = this.vpArr.get(i).getData();
                    double dist = this.metric.eval(target, point);
                    if (dist <= eps) {
                        results.add(point);
                    }
                }
            } else {
                double dist = this.metric.eval(center, target);
                if (dist <= eps) {
                    results.add(center);
                }
                if (dist < radius + eps) {
                    ballSearchRec(results, start + 1, mid);
                }
                if (dist >= radius - eps) {
                    ballSearchRec(results, mid, bound);
                }
            }
        }
    }

	@Override
	public Collection<T> getCenters() {
		// TODO Auto-generated method stub
		return null;
	}

}
