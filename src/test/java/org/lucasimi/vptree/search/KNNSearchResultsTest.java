package org.lucasimi.vptree.search;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.lucasimi.utils.Metric;

public class KNNSearchResultsTest {

    private Metric<Integer> metric = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    @Test 
    public void test() {
        KNNSearchResults<Integer> results = new KNNSearchResults<>(metric, 0, 10);
        for (int i = 0; i < 20; i++) {
            results.add(i);
        }
        List<Integer> expected = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            expected.add(i);
        }
        Collection<Integer> points = results.getPoints();
        assertEquals(10, points.size());
        assertEquals(new HashSet<>(expected), new HashSet<>(points));
    }

    @Test 
    public void testRandom() {
        int size = 100;
        int target = 0;
        Random rand = new Random();
        KNNSearchResults<Integer> results = new KNNSearchResults<>(metric, target, size);
        List<Integer> data = new ArrayList<>(10 * size);
        for (int i = 0; i < (10 * size); i++) {
            Integer val = rand.nextInt(size);
            val = i;
            results.add(val);
            data.add(val);
        }
        List<Integer> expected = data.stream() 
            .sorted((x, y) -> Double.compare(metric.eval(target, x), metric.eval(target, y)))
            .limit(size)
            .toList();
        Collection<Integer> points = results.getPoints();

        double radius = Double.NEGATIVE_INFINITY;
        for (Integer val : expected) {
            radius = Math.max(radius, metric.eval(val, target));
        }
        assertEquals(size, expected.size());
        assertEquals(size, points.size());
        assertEquals(radius, results.getRadius(), 0.00000001);
        assertEquals(new HashSet<>(expected), new HashSet<>(points));
    }

}
