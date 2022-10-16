package org.lucasimi.vptree;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;

public class VPTreeImpTest {

    private static final int MAX_POWER = 10;

    private static final int BASE = 2;

    private Metric<Integer> metric = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    @Test
    public void testBallSearchSingleton() {
        ArrayList<Integer> dataset = new ArrayList<>(1);
        dataset.add(1);
        VPTreeImp<Integer> vpTree = new VPTreeImp<>(metric, dataset);
        Collection<Integer> res = vpTree.ballSearch(1, 10.0);
        Assert.assertTrue(res.contains(1));
    }

    @Test
    public void testBallSearchLine() {
        ArrayList<Integer> dataset = new ArrayList<>();
        int size = (int) Math.pow(BASE, MAX_POWER);
        for (int i = 0; i < size; i++) {
            dataset.add(i);
        }
        VPTreeImp<Integer> vpTree = new VPTreeImp<>(metric, dataset, 10);
        for (Integer point : dataset) {
            Collection<Integer> res = vpTree.ballSearch(point, 1.5);
            Assert.assertTrue(res.size() <= 3);
            Assert.assertTrue(res.contains(point));
        }
    }

    @Test
    public void testBallSearchDuplicates() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, 1);
        VPTreeImp<Integer> vpTree = new VPTreeImp<>(metric, dataset);
        Collection<Integer> res = vpTree.ballSearch(0, 1.5);
        Assert.assertEquals(size, res.size());
    }

    @Test
    public void testKNNSearch() {
        List<Integer> dataset = DatasetGenerator.linearDataset(1000);
        VPTreeImp<Integer> vpTree = new VPTreeImp<>(metric, dataset, 100);
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
        VPTreeImp<Integer> vpTree = new VPTreeImp<>(metric, dataset, 100);
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
