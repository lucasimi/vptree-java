package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
        String prefix = "VPTree - ballSearch:\t";
        long t0 = System.currentTimeMillis();
        Collection<T> results = new LinkedList<>();
        for (T testPoint : sample) {
            results = vpTree.ballSearch(testPoint, eps);
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format(prefix + "Run in %dms (%d results)", t1 - t0, results.size()));
        return t1 - t0;
    }

    private <T> long benchmarkBallSearchFlat(List<T> sample, Metric<T> metric, FlatVPTree<T> vpTree, double eps) {
        String prefix = "FlatVPTree - ballSearch:\t";
        long t0 = System.currentTimeMillis();
        Collection<T> results = new LinkedList<>();
        for (T testPoint : sample) {
            results = vpTree.ballSearch(testPoint, eps);
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format(prefix + "Run in %dms (%d results)", t1 - t0, results.size()));
        return t1 - t0;
    }

    private <T> long benchmarkKNNSearch(List<T> sample, Metric<T> metric, VPTree<T> vpTree, int neighbors) {
        String prefix = "VPTree - knnSearch: \t";
        long t0 = System.currentTimeMillis();
        Collection<T> results = new LinkedList<>();
        for (T testPoint : sample) {
            results = vpTree.knnSearch(testPoint, neighbors);
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format(prefix + "Run in %dms (%d results)", t1 - t0, results.size()));
        return t1 - t0;
    }

    private <T> VPTree<T> benchmarkBuild(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        String prefix = "VPTree - build:     \t";
        long t0 = System.currentTimeMillis();
        VPTree<T> vpTree = new VPTree.Builder<T>()
                .withMetric(metric)
                .withLeafRadius(eps)
                .withLeafCapacity(neighbors)
                .build(dataset);
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format(prefix + "%dms", t1 - t0));
        return vpTree;
    }

    private <T> FlatVPTree<T> benchmarkBuildFlat(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        String prefix = "FlatVPTree - build:     \t";
        long t0 = System.currentTimeMillis();
        FlatVPTree<T> vpTree = new FlatVPTree.Builder<T>()
                .withMetric(metric)
                .build(dataset);
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format(prefix + "%dms", t1 - t0));
        return vpTree;
    }

    private <T> void benchmark(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        VPTree<T> vpTree = benchmarkBuild(dataset, metric, eps, neighbors);
        benchmarkBallSearch(dataset, metric, vpTree, eps);
        benchmarkKNNSearch(dataset, metric, vpTree, neighbors);
        FlatVPTree<T> vpTreeFlat = benchmarkBuildFlat(dataset, metric, eps, neighbors);
        benchmarkBallSearchFlat(dataset, metric, vpTreeFlat, eps);
    }

    @Test
    public void benchAll() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<double[]> dataset = DatasetGenerator.randomDataset(size, DIMENSIONS, 8.0, 12.0);
        List<double[]> sample = sample(dataset, (int) (0.01 * dataset.size()));
        double radius = 1.5 * Math.sqrt(DIMENSIONS);
        int neighbors = (int) (0.001 * dataset.size());
        benchmark(sample, metric, radius, neighbors);
    }

}
