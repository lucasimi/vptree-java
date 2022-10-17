package org.lucasimi.vptree;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;

public class BenchTest {

    private static final int BASE = 10;

    private static final int MAX_POWER = 6;

    private static final Logger LOGGER = Logger.getLogger(BenchTest.class.getName());

    private Metric<Integer> metric = new Metric<Integer>() {

        @Override
        public double eval(Integer x, Integer y) {
            return Math.abs(x - y);
        }

    };

    @Test
    public void benchAll() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<Integer> dataset = DatasetGenerator.randomDataset(size, 0, size / 10);
        long t0 = System.currentTimeMillis();
        VPTree<Integer> vpTree = new VPTree<>(metric, dataset);
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format("VPTree    [build]:      %dms", t1 - t0));
        t0 = System.currentTimeMillis();
        VPTreeImp<Integer> vpTreeImp = new VPTreeImp<>(metric, dataset);
        t1 = System.currentTimeMillis();
        LOGGER.info(String.format("VPTreeImp [build]:      %dms", t1 - t0));
        t0 = System.currentTimeMillis();
        for (int i = 0; i < size / 100; i++) {
            Integer testPoint = dataset.get(i);
            vpTree.ballSearch(testPoint, 100);
        }
        t1 = System.currentTimeMillis();
        LOGGER.info(String.format("VPTree    [ballSearch]: %dms", t1 - t0));
        t0 = System.currentTimeMillis();
        for (int i = 0; i < size / 100; i++) {
            Integer testPoint = dataset.get(i);
            vpTreeImp.ballSearch(testPoint, 100);
        }
        t1 = System.currentTimeMillis();
        LOGGER.info(String.format("VPTreeImp [ballSearch]: %dms", t1 - t0));
    }

}
