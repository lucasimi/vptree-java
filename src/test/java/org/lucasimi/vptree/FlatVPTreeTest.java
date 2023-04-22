package org.lucasimi.vptree;

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
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, 1);
        FlatVPTree<Integer> vpTree = new FlatVPTree.Builder<Integer>()
                .withMetric(metric)
                .build(dataset);
    }

}
