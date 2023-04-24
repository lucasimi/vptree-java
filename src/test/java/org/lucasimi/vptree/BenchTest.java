package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.flat.FlatVPTree;
import org.lucasimi.vptree.split.SplitVPTree;

public class BenchTest {

    private static final int BASE = 10;

    private static final int MAX_POWER = 6;

    private static final int DIMENSIONS = 100;

    private static final Logger LOGGER = Logger.getLogger(BenchTest.class.getName());

    private static final Random rand = new Random();

    private static final Map<String, Map<String, Long>> report = new HashMap<>();

    {
        report.put(SplitVPTree.class.getSimpleName(), new TreeMap<>());
        report.put(FlatVPTree.class.getSimpleName(), new TreeMap<>());
    }

    private static final String BUILD = "build";

    private static final String BALL_SEARCH = "ball search";

    private static final String KNN_SEARCH = "knn search";

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

    private <T> void benchmarkBallSearch(List<T> dataset, VPTree<T> vpTree, double eps) {
        List<T> sample = sample(dataset, (int) (0.1 * dataset.size()));
        long t0 = System.currentTimeMillis();
        for (T testPoint : sample) {
            vpTree.ballSearch(testPoint, eps);
        }
        long t1 = System.currentTimeMillis();
        String name = vpTree.getClass().getSimpleName();
        report.get(name).put(BALL_SEARCH, t1 - t0);
    }

    private <T> void benchmarkKNNSearch(List<T> dataset, VPTree<T> vpTree, int neighbors) {
        List<T> sample = sample(dataset, (int) (0.1 * dataset.size()));
        long t0 = System.currentTimeMillis();
        for (T testPoint : sample) {
            vpTree.knnSearch(testPoint, neighbors);
        }
        long t1 = System.currentTimeMillis();
        String name = vpTree.getClass().getSimpleName();
        report.get(name).put(KNN_SEARCH, t1 - t0);
    }

    private <T> void benchmarkBallSearch(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        SplitVPTree<T> splitVPTree = SplitVPTree.<T>newBuilder()
            .withMetric(metric)
            .withLeafRadius(eps)
            .withLeafCapacity(neighbors)
            .build(dataset);
        benchmarkBallSearch(dataset, splitVPTree, eps);
        FlatVPTree<T> flatVPTree = FlatVPTree.<T>newBuilder()
            .withMetric(metric)
            .withLeafRadius(eps)
            .withLeafCapacity(neighbors)
            .build(dataset);
        benchmarkBallSearch(dataset, flatVPTree, eps);
    }

    private <T> void benchmarkKNNSearch(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        SplitVPTree<T> splitVPTree = SplitVPTree.<T>newBuilder()
            .withMetric(metric)
            .withLeafRadius(eps)
            .withLeafCapacity(neighbors)
            .build(dataset);
        benchmarkKNNSearch(dataset, splitVPTree, neighbors);
        FlatVPTree<T> flatVPTree = FlatVPTree.<T>newBuilder()
            .withMetric(metric)
            .withLeafRadius(eps)
            .withLeafCapacity(neighbors)
            .build(dataset);
        benchmarkKNNSearch(dataset, flatVPTree, neighbors);
    }

    private <T> void benchmarkBuild(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        int sampleSize = (int) (0.1 * dataset.size());
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < sampleSize; i++) {
            SplitVPTree.<T>newBuilder()
                .withMetric(metric)
                .withLeafRadius(eps)
                .withLeafCapacity(neighbors)
                .build(dataset);
        }
        long t1 = System.currentTimeMillis();
        String splitName = SplitVPTree.class.getSimpleName();
        report.get(splitName).put(BUILD, t1 - t0);
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < sampleSize; i++) {
            FlatVPTree.<T>newBuilder()
                .withLeafCapacity(neighbors)
                .withLeafRadius(eps)
                .withMetric(metric)
                .build(dataset);
        }
        long t3 = System.currentTimeMillis();
        String flatName = FlatVPTree.class.getSimpleName();
        report.get(flatName).put(BUILD, t3 - t2);
    }

    private <T> void benchmark(List<T> dataset, Metric<T> metric, double eps, int neighbors) {
        benchmarkBuild(dataset, metric, eps, neighbors);
        benchmarkBallSearch(dataset, metric, eps, neighbors);
        benchmarkKNNSearch(dataset, metric, eps, neighbors);
    }

    private void printReport() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, Map<String, Long>> nameEntry : report.entrySet()) {
            builder.append("\n");
            for (Entry<String, Long> methodEntry : nameEntry.getValue().entrySet()) {
                String info = String.format("%s [%s]: \t %dms", nameEntry.getKey(), methodEntry.getKey(), methodEntry.getValue());
                builder.append(info);
                builder.append("\n");
            }
        }
        LOGGER.info(String.format("VPTree benchmark report: \n%s", builder.toString()));
    }

    @Test
    public void benchAll() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<double[]> dataset = DatasetGenerator.randomDataset(size, DIMENSIONS, 8.0, 12.0);
        List<double[]> sample = sample(dataset, (int) (0.01 * dataset.size()));
        double radius = 1.5 * Math.sqrt(DIMENSIONS);
        int neighbors = (int) (0.001 * dataset.size());
        benchmark(sample, metric, radius, neighbors);
        printReport();
    }

}

