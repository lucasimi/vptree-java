package org.lucasimi.vptree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;

public class FlatVPTreeTest {

    private static final int MAX_POWER = 10;

    private static final int BASE = 2;

    private Metric<Integer> metric = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    @Test
    public void testCreateEmpty() {
        List<Integer> dataset = new LinkedList<>();
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
    }

    @Test
    public void testCreateSingleton() {
        List<Integer> dataset = new LinkedList<>();
        dataset.add(1);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
    }

    @Test
    public void testCreate() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, 10);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
    }

    @Test
    public void testBallSearchSingleton() {
        List<Integer> dataset = new ArrayList<>(1);
        dataset.add(1);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
        Collection<Integer> res = vpTree.ballSearch(1, 10.0);
        assertTrue(res.contains(1));
    }

    @Test
    public void testBallSearchRandom() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, size / 10);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
        testBallSearch(dataset, metric, vpTree, 2.5);
    }

    @Test
    public void testBallSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.linearDataset(size);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
        testBallSearch(dataset, metric, vpTree, 2.5);
    }

    @Test
    public void testBallSearchDuplicates() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, 1);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
        Collection<Integer> res = vpTree.ballSearch(0, 1.5);
        assertEquals(size, res.size());
    }

    private <T> void testBallSearch(Collection<T> dataset, Metric<T> metric, FlatVPTree<T> vpTree, double radius) {
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

}
