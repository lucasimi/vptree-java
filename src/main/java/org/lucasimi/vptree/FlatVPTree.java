package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;
import org.lucasimi.utils.Pivoter;

public class FlatVPTree<T> {

    private final Metric<T> metric;

    private ArrayList<Ordered<Double, T>> dataset;

    private static final Random rand = new Random();

    private FlatVPTree(Builder<T> builder, Collection<T> data) {
        this.metric = builder.metric;
        this.dataset = new ArrayList<>(data.size());
        for (T x : data) {
            this.dataset.add(new Ordered<>(0.0, x));
        }
        int start = 0;
        int bound = this.dataset.size();
        process(start, bound);
    }

    private void process(int start, int bound) {
        if (bound <= start + 1) {
            return;
        }
        int mid = (start + 1 + bound) / 2;
        int pivot = rand.nextInt(start, bound);
        swap(start, pivot);
        Ordered<Double, T> center = this.dataset.get(start);
        for (int i = start; i < bound; i++) {
            Ordered<Double, T> point = this.dataset.get(i);
            point.setOrder(this.metric.eval(center.getData(), point.getData()));
        }
        Pivoter.quickSelect(this.dataset, start + 1, bound, mid);
        double radius = this.dataset.get(mid).getOrder();
        this.dataset.get(start).setOrder(radius);
        process(start + 1, mid);
        process(mid, bound);
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
            Ordered<Double, T> iVal = this.dataset.get(i);
            Ordered<Double, T> jVal = this.dataset.get(j);
            this.dataset.set(i, jVal);
            this.dataset.set(j, iVal);
        }
    }

}
