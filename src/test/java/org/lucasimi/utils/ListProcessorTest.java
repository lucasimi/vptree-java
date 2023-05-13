package org.lucasimi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;

public class ListProcessorTest {

    private static final Metric<Integer> METRIC = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    @Test
    public void testLinear() {
        double tol = 0.00000000001;
        int size = 1000;
        Collection<Integer> dataset = DatasetGenerator.linear(size);
        List<Ordered<Double, Integer>> data = new ArrayList<>(dataset.size());
        for (Integer val : dataset) {
            data.add(new Ordered<>(0.0, val));
        }
        ListProcessor<Integer> proc = new ListProcessor<>(METRIC, data);
        int split = size / 2;
        double radius = proc.processUpdate(0, size, split);
        Ordered<Double, Integer> vp = data.get(0);
        Integer vpData = vp.getData();
        double vpOrd = vp.getOrder();
        Integer furthest = data.get(split).getData();

        assertEquals(METRIC.eval(vpData, furthest), vpOrd, tol);
        for (int i = 1; i < split; i++) {
            Ordered<Double, Integer> point = data.get(i);
            Integer pointData = point.getData();
            double pointOrd = point.getOrder();
            assertTrue(METRIC.eval(vpData, pointData) <= radius);
            assertEquals(METRIC.eval(vpData, pointData), pointOrd, tol);
        }
        for (int i = split; i < size; i++) {
            Ordered<Double, Integer> point = data.get(i);
            Integer pointData = point.getData();
            double pointOrd = point.getOrder();
            assertTrue(METRIC.eval(vpData, pointData) >= radius);
            assertEquals(METRIC.eval(vpData, pointData), pointOrd, tol);
        }

    }

}
