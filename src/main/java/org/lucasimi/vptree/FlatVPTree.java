package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;

public class FlatVPTree<T> {

    private final Metric<T> metric;

    private ArrayList<Ordered<Double, T>> vpArr;

    private static final Random rand = new Random();

    private FlatVPTree(Builder<T> builder, Collection<T> data) {
        this.metric = builder.metric;
        this.vpArr = new ArrayList<>(data.size());
        for (T x : data) {
            this.vpArr.add(new Ordered<>(0.0, x));
        }
        build();
    }

    private void build() {
        buildRec(0, this.vpArr.size());
    }

    private void buildRec(int start, int bound) {
        if (bound <= start + 1) {
            return;
        }
        int mid = (start + 1 + bound) / 2;
        int pivot = rand.nextInt(start, bound);
        swap(start, pivot);
        Ordered<Double, T> center = this.vpArr.get(start);
        for (int i = start; i < bound; i++) {
            Ordered<Double, T> point = this.vpArr.get(i);
            point.setOrder(this.metric.eval(center.getData(), point.getData()));
        }
        Pivoter.quickSelect(this.vpArr, start + 1, bound, mid);
        double radius = this.vpArr.get(mid).getOrder();
        this.vpArr.get(start).setOrder(radius);
        buildRec(start + 1, mid);
        buildRec(mid, bound);
    }

    public Collection<T> ballSearch(T target, double eps) {
        Collection<T> results = new LinkedList<>();
        ballSearchRec(target, eps, results, 0, this.vpArr.size());
        return results;
    }

    private void ballSearchRec(T target, double eps, Collection<T> results, int start, int bound) {
        if (bound == start + 1) {
            Ordered<Double, T> ord = this.vpArr.get(start);
            T center = ord.getData();
            double dist = this.metric.eval(center, target);
            if (dist <= eps) {
                results.add(center);
            }
        } else if (bound > start + 1) {
            int mid = (start + 1 + bound) / 2;
            Ordered<Double, T> ord = this.vpArr.get(start);
            T center = ord.getData();
            double radius = ord.getOrder();
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

    public static class Builder<T> {

        private Metric<T> metric;

        public Builder<T> withMetric(Metric<T> metric) {
            this.metric = metric;
            return this;
        }

        public FlatVPTree<T> build(Collection<T> data) {
            if (this.metric == null) {
                throw new IllegalArgumentException("A metric must be specified");
            }
            return new FlatVPTree<>(this, data);
        }

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

}
