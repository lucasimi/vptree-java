package org.lucasimi.vptree;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.lucasimi.utils.Metric;

public class TestUtils {

    private TestUtils() {}

    public static <T> List<T> knnSearch(Collection<T> dataset, Metric<T> metric, T target, int neighbors) {
        List<T> arr = new ArrayList<>(dataset);
        return arr.stream()
            .sorted((p, q) -> Double.compare(metric.eval(target, p), metric.eval(target, q)))
            .limit(neighbors)
            .toList();
    }

    public static <T> List<T> ballSearch(Collection<T> dataset, Metric<T> metric, T target, double radius) {
        List<T> arr = new ArrayList<>(dataset);
        return arr.stream()
            .filter(p -> metric.eval(target, p) <= radius)
            .toList();
    }

    public static <T> void testKNNSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, int neighbors) {
        for (T point : dataset) {
            Collection<T> res = vpTree.knnSearch(point, neighbors);
            Collection<T> expected = knnSearch(dataset, metric, point, neighbors);
            assertEquals(new HashSet<>(expected), new HashSet<>(res));
        }
    }

    public static <T> void testBallSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, double radius) {
        for (T point : dataset) {
            Collection<T> res = vpTree.ballSearch(point, radius);
            Collection<T> expected = ballSearch(dataset, metric, point, radius);
            assertEquals(new HashSet<>(expected), new HashSet<>(res));
        }
    }

}
