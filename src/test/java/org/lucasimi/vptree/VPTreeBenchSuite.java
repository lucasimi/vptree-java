package org.lucasimi.vptree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;
import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.VPTree.Builder;
import org.lucasimi.vptree.VPTree.TreeType;

public class VPTreeBenchSuite {

    private static final int BASE = 10;

    private static final int MAX_POWER = 5;

    private static final int DIMENSIONS = 32;

    private static final double SAMPLE_SIZE = 100.0;

    private static final Logger LOGGER = Logger.getLogger(VPTreeBenchSuite.class.getName());

    private static final Random rand = new Random();

    public TreeType getTreeType() {
        return null;
    }

    private static final Metric<double[]> METRIC = new Metric<double[]>() {

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

    public <T> VPTree<T> benchBuild(Collection<T> dataset, VPTree.Builder<T> builder, int times) {
        long t0 = System.currentTimeMillis();
        VPTree<T> vpTree = builder.build(dataset);
        for (int i = 0; i < times - 1; i++) {
            builder.build(dataset);
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format("[%s]  build tree: \t%d ms", getTreeType(), t1 - t0));
        return vpTree;
    }

    public <T> void benchBallSearch(VPTree<T> vpTree, VPTree.Builder<T> builder, Collection<T> sample) {
        long t0 = System.currentTimeMillis();
        for (T testPoint : sample) {
            vpTree.ballSearch(testPoint, builder.getLeafRadius());
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format("[%s] ball search: \t%d ms", getTreeType(), t1 - t0));
    }

    public <T> void benchKNNSearch(VPTree<T> vpTree, VPTree.Builder<T> builder, Collection<T> sample) {
        long t0 = System.currentTimeMillis();
        for (T testPoint : sample) {
            vpTree.knnSearch(testPoint, builder.getLeafCapacity());
        }
        long t1 = System.currentTimeMillis();
        LOGGER.info(String.format("[%s]  knn search: \t%d ms", getTreeType(), t1 - t0));
    }

    @Test
    public void runBench() {
        int size = (int) Math.pow(BASE, MAX_POWER);
        List<double[]> dataset = new ArrayList<>(DatasetGenerator.random(size, DIMENSIONS, 8.0, 12.0));
        List<double[]> sample = sample(dataset, (int) SAMPLE_SIZE);
        Builder<double[]> builder = VPTree.<double[]>newBuilder()
                .withMetric(METRIC)
                .withLeafRadius(1.5 * Math.sqrt(DIMENSIONS))
                .withLeafCapacity((int) (0.001 * dataset.size()))
                .withTreeType(getTreeType());
        VPTree<double[]> vpTree = benchBuild(dataset, builder, sample.size());
        benchBallSearch(vpTree, builder, sample);
        benchKNNSearch(vpTree, builder, sample);
    }

}

