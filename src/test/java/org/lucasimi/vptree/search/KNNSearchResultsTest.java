package org.lucasimi.vptree.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        Collection<Integer> points = results.extractPoints();
        assertEquals(10, points.size());
        assertEquals(new HashSet<>(expected), new HashSet<>(points));
    }

    @Test 
    public void testRandom() {
        assertTrue(5.0 <= Double.POSITIVE_INFINITY);
        int size = 100;
        int target = 0;
        Random rand = new Random();
        KNNSearchResults<Integer> results = new KNNSearchResults<>(metric, target, size);
        List<Integer> data = new ArrayList<>(10 * size);
        for (int i = 0; i < (10 * size); i++) {
            Integer val = rand.nextInt(size);
            //val = i;
            results.add(val);
            data.add(val);
        }
        List<Integer> expected = data.stream() 
            .sorted((x, y) -> Double.compare(metric.eval(target, x), metric.eval(target, y)))
            .limit(size)
            .collect(Collectors.toList());
        assertEquals(size, expected.size());
        double radius = results.getRadius();

        double expectedRadius = Double.NEGATIVE_INFINITY;
        for (Integer val : expected) {
            expectedRadius = Math.max(expectedRadius, metric.eval(val, target));
        }
        Collection<Integer> points = results.extractPoints();
        assertEquals(expectedRadius, radius, 0.00000001);
        assertEquals(new HashSet<>(expected), new HashSet<>(points));
    }

    @Test 
    public void testSmall() {
        int target = 0;
        KNNSearchResults<Integer> results = new KNNSearchResults<>(metric, target, 2);
        List<Integer> data = new ArrayList<>();
        Collections.addAll(data, 1, 2, 3);
        for (Integer val : data) {
            results.add(val);
        }
        List<Integer> expected = data.stream() 
            .sorted((x, y) -> Double.compare(metric.eval(target, x), metric.eval(target, y)))
            .limit(2)
            .collect(Collectors.toList());
        Collection<Integer> points = results.extractPoints();
        assertEquals(new HashSet<>(expected), new HashSet<>(points));
    }
    
}
