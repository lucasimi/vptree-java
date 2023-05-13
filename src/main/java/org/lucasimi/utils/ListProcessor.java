package org.lucasimi.utils;

import java.util.List;
import java.util.Random;

public class ListProcessor<T> {

    private static final Random RAND = new Random();

    private final Metric<T> metric;

    private final List<Ordered<Double, T>> data;

    public ListProcessor(Metric<T> metric, List<Ordered<Double, T>> data) {
        this.metric = metric;
        this.data = data;
    }

    public double processUpdate(int start, int bound, int split) {
        Ordered<Double, T> vpOrd = this.data.get(start);
        T vp = vpOrd.getData();
        for (int j = start + 1; j < bound; j++) {
            Ordered<Double, T> wo = this.data.get(j);
            wo.setOrder(this.metric.eval(vp, wo.getData()));
        }
        Pivoter.quickSelect(this.data, start + 1, bound, split);
        Ordered<Double, T> furthest = this.data.get(split);
        double radius = furthest.getOrder();
        vpOrd.setOrder(radius);
        return radius;
    }

}
