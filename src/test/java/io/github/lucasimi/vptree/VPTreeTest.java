package io.github.lucasimi.vptree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import io.github.lucasimi.DatasetGenerator;
import io.github.lucasimi.utils.Metric;

public class VPTreeTest {

    private static final int MAX_POWER = 10;

    private static final int BASE = 2;

    private Metric<Integer> metric = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    private <T> List<T> knnSearch(Metric<T> metric, Collection<T> dataset, T center, int neighbors) {
        List<T> sorted = new ArrayList<>(dataset);
        sorted.sort((p, q) -> Double.compare(metric.eval(center, p), metric.eval(center, q)));
        List<T> filtered = new LinkedList<>();
        for (int i = 0; i < neighbors; i++) {
            filtered.add(sorted.get(i));
        }
        return filtered;
    }

    private <T> double knnRadius(Metric<T> metric, Collection<T> dataset, T center, int neighbors) {
        List<T> results = knnSearch(metric, dataset, center, neighbors);
        T furthest = results.get(neighbors - 1);
        return metric.eval(center, furthest);
    }

    private <T> void testBallSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, double radius) {
        for (T point : dataset) {
            Collection<T> res = vpTree.ballSearch(point, radius);
            for (T x : dataset) {
                if (res.contains(x)) {
                    assertTrue(metric.eval(point, x) <= radius);
                } else {
                    assertTrue(metric.eval(point, x) > radius);
                }
            }
            assertTrue(res.contains(point));
        }
    }

    private <T> void testKNNSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, int neighbors) {
        for (T point : dataset) {
            Collection<T> res = vpTree.knnSearch(point, neighbors);
            double knnRadius = knnRadius(metric, dataset, point, neighbors);
            for (T x : dataset) {
                if (res.contains(x)) {
                    assertTrue(metric.eval(point, x) <= knnRadius);
                } else {
                    assertTrue(metric.eval(point, x) >= knnRadius);
                }
            }
            assertTrue(res.contains(point));
        }
    }

    @Test
    public void testBallSearchSingleton() {
        List<Integer> dataset = new ArrayList<>(1);
        dataset.add(1);
        VPTree<Integer> vpTree = new VPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
        Collection<Integer> res = vpTree.ballSearch(1, 10.0);
        assertTrue(res.contains(1));
    }

    @Test
    public void testBallSearchRandom() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, size / 10);
        VPTree<Integer> vpTree = new VPTree.Builder<Integer>()
                .withMetric(metric)
                .withLeafCapacity(10)
                .build(dataset);
        testBallSearch(dataset, metric, vpTree, 2.5);
    }

    @Test
    public void testBallSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.linearDataset(size);
        VPTree<Integer> vpTree = new VPTree.Builder<Integer>()
                .withMetric(metric)
                .withLeafCapacity(100)
                .build(dataset);
        testBallSearch(dataset, metric, vpTree, 2.5);
    }

    @Test
    public void testBallSearchDuplicates() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, 1);
        VPTree<Integer> vpTree = new VPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
        Collection<Integer> res = vpTree.ballSearch(0, 1.5);
        assertEquals(size, res.size());
    }

    @Test
    public void testKNNSearch() {
        List<Integer> dataset = DatasetGenerator.linearDataset(1000);
        VPTree<Integer> vpTree = new VPTree.Builder<Integer>()
                .withMetric(metric)
                .withLeafCapacity(10)
                .build(dataset);
        testKNNSearch(dataset, metric, vpTree, 20);
    }

    @Test
    public void testKNNSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.linearDataset(size);
        VPTree<Integer> vpTree = new VPTree.Builder<Integer>()
                .withMetric(metric)
                .withLeafCapacity(100)
                .build(dataset);
        testKNNSearch(dataset, metric, vpTree, 20);
    }

}
