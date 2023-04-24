package org.lucasimi.vptree.flat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;
import org.lucasimi.vptree.VPTree;

public class FlatVPTree<T> implements VPTree<T> {

    private static final Random RAND = new Random();

    private final int leafCapacity;

    private final double leafRadius;

    private final Metric<T> metric;

    private final ArrayList<Ordered<Double, T>> vpArr;

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    private FlatVPTree(Builder<T> builder, Collection<T> data) {
        this.metric = builder.metric;
        this.vpArr = new ArrayList<>(data.size());
        this.leafCapacity = builder.leafCapacity;
        this.leafRadius = builder.leafRadius;
        for (T x : data) {
            this.vpArr.add(new Ordered<>(0.0, x));
        }
        if (builder.randomPivoting) {
            buildRand();
        } else {
            buildNoRand();
        }
    }

	@Override
    public Collection<T> ballSearch(T target, double eps) {
        Collection<T> results = new LinkedList<>();
        ballSearchRec(target, eps, results, 0, this.vpArr.size());
        return results;
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

    private void buildNoRandRec(int start, int bound) {
        if (bound <= start + this.leafCapacity) {
            return;
        }
        int mid = (start + 1 + bound) / 2;
        Ordered<Double, T> center = this.vpArr.get(start);
        for (int i = start; i < bound; i++) {
            Ordered<Double, T> point = this.vpArr.get(i);
            point.setOrder(this.metric.eval(center.getData(), point.getData()));
        }
        Pivoter.quickSelect(this.vpArr, start + 1, bound, mid);
        double radius = this.vpArr.get(mid).getOrder();
        this.vpArr.get(start).setOrder(radius);
        if (radius >= this.leafRadius) {
            buildNoRandRec(start + 1, mid);
        }
        buildNoRandRec(mid, bound);
    }

    private void buildRandRec(int start, int bound) {
        if (bound <= start + this.leafCapacity) {
            return;
        }
        int mid = (start + 1 + bound) / 2;
        int pivot = start + RAND.nextInt(bound - start);
        swap(start, pivot);
        Ordered<Double, T> center = this.vpArr.get(start);
        for (int i = start; i < bound; i++) {
            Ordered<Double, T> point = this.vpArr.get(i);
            point.setOrder(this.metric.eval(center.getData(), point.getData()));
        }
        Pivoter.quickSelect(this.vpArr, start + 1, bound, mid);
        double radius = this.vpArr.get(mid).getOrder();
        this.vpArr.get(start).setOrder(radius);
        if (radius >= this.leafRadius) {
            buildRandRec(start + 1, mid);
        }
        buildRandRec(mid, bound);
    }

    private void ballSearchRec(T target, double eps, Collection<T> results, int start, int bound) {
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
            int mid = (start + 1 + bound) / 2;
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
                    ballSearchRec(target, eps, results, start + 1, mid);
                }
                if (dist >= radius - eps) {
                    ballSearchRec(target, eps, results, mid, bound);
                }
            }
        }
    }

    public static class Builder<T> {

        private Metric<T> metric;

        private int leafCapacity = 1;

        private double leafRadius = 0.0;

        private boolean randomPivoting = true;

        private Builder() {}

        public Builder<T> withMetric(Metric<T> metric) {
            this.metric = metric;
            return this;
        }

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

        public FlatVPTree<T> build(Collection<T> data) {
            if (this.metric == null) {
                throw new IllegalArgumentException("A metric must be specified");
            }
            return new FlatVPTree<>(this, data);
        }

    }

}
