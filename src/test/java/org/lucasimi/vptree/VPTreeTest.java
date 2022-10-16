package org.lucasimi.vptree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;

public class VPTreeTest {

    private static final int MAX_POWER = 10;

    private static final int BASE = 2;

    private static final Random rand = new Random();

    private Metric<Integer> metric = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    @Test
    public void testBallSearchRandom() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, size / 10);
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset, 10);
        for (Integer point : dataset) {
            double radius = 1.5 + 2.5 * rand.nextDouble();
            Collection<Integer> res = vpTree.ballSearch(point, radius);
            Collection<Integer> expected = ballSearch(metric, dataset, point, radius);
            assertEquals(new HashSet<Integer>(expected), new HashSet<Integer>(res));
            assertTrue(res.contains(point));
        }
    }

    @Test
    public void testBallSearchSingleton() {
        List<Integer> dataset = new ArrayList<>(1);
        dataset.add(1);
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset);
        Collection<Integer> res = vpTree.ballSearch(1, 10.0);
        assertTrue(res.contains(1));
    }

    @Test
    public void testBallSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.linearDataset(size);
        System.out.println("size = " + dataset.size());
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset, 100);
        double radius = 1.5;
        for (Integer point : dataset) {
            Collection<Integer> res = vpTree.ballSearch(point, radius);
            Collection<Integer> expected = ballSearch(metric, dataset, point, radius);
            assertEquals(new HashSet<Integer>(expected), new HashSet<Integer>(res));
            assertTrue(res.contains(point));
        }
    }

    private <T> Collection<T> ballSearch(Metric<T> metric, Collection<T> dataset, T center, double eps) {
        List<T> filtered = new LinkedList<>();
        for (T x : dataset) {
            if (metric.eval(center, x) <= eps) {
                filtered.add(x);
            }
        }
        return filtered;
    }

    @Test
    public void testBallSearchDuplicates() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, 1);
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset);
        Collection<Integer> res = vpTree.ballSearch(0, 1.5);
        assertEquals(size, res.size());
    }

    @Test
    public void testKNNSearch() {
        List<Integer> dataset = DatasetGenerator.linearDataset(1000);
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset, 10);
        for (Integer point : dataset) {
            Collection<Integer> res = vpTree.knnSearch(point, 10);
            assertTrue(res.contains(point));
            assertTrue(res.size() <= 10);
        }
    }

    @Test
    public void testKNNSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.linearDataset(size);
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset, 100);
        for (Integer point : dataset) {
            Collection<Integer> res = vpTree.knnSearch(point, 10);
            assertTrue(res.size() <= 10);
            assertTrue(res.contains(point));
            boolean flag = false;
            for (Integer x : res) {
                if (x.equals(point)) {
                    flag = true;
                }
            }
            assertTrue(flag);
        }
    }

}
