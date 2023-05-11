package org.lucasimi.vptree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.VPTree.TreeType;

public class VPTreeTestSuite {
     
    public TreeType getTreeType() {
        return null;
    }

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
        List<Integer> dataset = new ArrayList<>(1);
        dataset.add(1);
        VPTree<Integer> vpTree = VPTree.<Integer>newBuilder()
                .withMetric(metric)
                .withTreeType(getTreeType())
                .build(dataset);
        Collection<Integer> res = vpTree.ballSearch(1, 10.0);
        assertTrue(res.contains(1));
    }

    @Test
    public void testBallSearchRandom() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        Collection<Integer> dataset = DatasetGenerator.random(size, 0, size / 10);
        VPTree<Integer> vpTree = VPTree.<Integer>newBuilder()
                .withMetric(metric)
                .withLeafCapacity(10)
                .withTreeType(getTreeType())
                .build(dataset);
        TestUtils.testBallSearch(dataset, metric, vpTree, 2.5);
    }

    @Test
    public void testBallSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        Collection<Integer> dataset = DatasetGenerator.linear(size);
        VPTree<Integer> vpTree = VPTree.<Integer>newBuilder()
                .withMetric(metric)
                .withLeafCapacity(100)
                .withTreeType(getTreeType())
                .build(dataset);
        TestUtils.testBallSearch(dataset, metric, vpTree, 2.5);
    }

    @Test
    public void testBallSearchDuplicates() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        Collection<Integer> dataset = DatasetGenerator.random(size, 0, 1);
        VPTree<Integer> vpTree = VPTree.<Integer>newBuilder()
                .withMetric(metric)
                .withTreeType(getTreeType())
                .build(dataset);
        Collection<Integer> res = vpTree.ballSearch(0, 1.5);
        assertEquals(size, res.size());
    }

    @Test
    public void testKNNSearch() {
        Collection<Integer> dataset = DatasetGenerator.linear(1000);
        VPTree<Integer> vpTree = VPTree.<Integer>newBuilder()
                .withMetric(metric)
                .withLeafCapacity(10)
                .withTreeType(getTreeType())
                .build(dataset);
        TestUtils.testKNNSearch(dataset, metric, vpTree, 20);
    }

    @Test
    public void testKNNSearchLine() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        Collection<Integer> dataset = DatasetGenerator.linear(size);
        VPTree<Integer> vpTree = VPTree.<Integer>newBuilder()
                .withMetric(metric)
                .withLeafCapacity(100)
                .withTreeType(getTreeType())
                .build(dataset);
        TestUtils.testKNNSearch(dataset, metric, vpTree, 20);
    }

}
