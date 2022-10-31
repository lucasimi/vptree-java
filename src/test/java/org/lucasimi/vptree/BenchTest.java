package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;

public class BenchTest {

    private static final int BASE = 10;

    private static final int MAX_POWER = 5;

    private static final int DIMENSIONS = 32;

    private static final Logger LOGGER = Logger.getLogger(BenchTest.class.getName());

    private static final Random rand = new Random();

    private Metric<double[]> metric = new Metric<double[]>() {

        @Override
        public double eval(double[] x, double[] y) {
            double sum = 0.0;
            double delta = 0.0;
            int size = Math.min(x.length, y.length);
            for (int i = 0; i < size; i++) {
                delta = x[i] - y[i];
                sum += delta * delta;
            }
            return Math.sqrt(sum);
        }

    };

    private <T> List<T> sample(List<T> dataset, int sampleSize) {
        int size = dataset.size();
        List<T> sample = new ArrayList<>(sampleSize);
        rand.ints(sampleSize, 0, size).forEach(x -> {
            sample.add(dataset.get(x));
        });
        return sample;
    }

    private <T> long benchmarkBallSearch(List<T> sample, Metric<T> metric, VPTree<T> vpTree, double eps) {
        long t0 = System.currentTimeMillis();
        for (T testPoint : sample) {
            vpTree.ballSearch(testPoint, eps);
        }
        long t1 = System.currentTimeMillis();
        return t1 - t0;
    }

    private <T> long benchmarkKNNSearch(List<T> sample, Metric<T> metric, VPTree<T> vpTree, int neighbors) {
        long t0 = System.currentTimeMillis();
        for (T testPoint : sample) {
            vpTree.knnSearch(testPoint, neighbors);
        }
        long t1 = System.currentTimeMillis();
        return t1 - t0;
    }

    private <T> void benchmarkVPTreeSimple(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        long t0 = System.currentTimeMillis();
        VPTree<T> vpTree = new VPTreeSimple.Builder<T>()
                .withMetric(metric)
                .withLeafCapacity(neighbors)
                .withLeafRadius(eps)
                .build(dataset);
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format("VPTreeSimple\t [build]:     \t %dms", t1 - t0));
        long deltaBallSearch = benchmarkBallSearch(dataset, metric, vpTree, eps);
        LOGGER.info(String.format("VPTreeSimple\t [ballSearch]:\t %dms", deltaBallSearch));
        long deltaKNNSearch = benchmarkKNNSearch(dataset, metric, vpTree, neighbors);
        LOGGER.info(String.format("VPTreeSimple\t [knnSearch]: \t %dms", deltaKNNSearch));
    }

    private <T> void benchmarkVPTreeADT(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        long t0 = System.currentTimeMillis();
        VPTree<T> vpTree = new VPTreeADT.Builder<T>()
                .withMetric(metric)
                .withLeafRadius(eps)
                .withLeafCapacity(neighbors)
                .build(dataset);
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format("VPTreeADT\t [build]:     \t %dms", t1 - t0));
        long deltaBallSearch = benchmarkBallSearch(dataset, metric, vpTree, eps);
        LOGGER.info(String.format("VPTreeADT\t [ballSearch]:\t %dms", deltaBallSearch));
        long deltaKNNSearch = benchmarkKNNSearch(dataset, metric, vpTree, neighbors);
        LOGGER.info(String.format("VPTreeADT\t [knnSearch]: \t %dms", deltaKNNSearch));
    }

    @Test
    public void benchAll() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<double[]> dataset = DatasetGenerator.randomDataset(size, DIMENSIONS, 8.0, 12.0);
        List<double[]> sample = sample(dataset, (int) (0.01 * dataset.size()));
        double radius = 10.0 * Math.sqrt(DIMENSIONS);
        int neighbors = size / 100;
        benchmarkVPTreeSimple(sample, metric, radius, neighbors);
        benchmarkVPTreeADT(sample, metric, radius, neighbors);
    }

}
