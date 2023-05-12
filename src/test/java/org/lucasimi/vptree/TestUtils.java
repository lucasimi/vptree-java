package org.lucasimi.vptree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.lucasimi.utils.Metric;

public class TestUtils {

    private TestUtils() {}

    public static <T> List<T> knnSearch(Collection<T> dataset, Metric<T> metric, T target, int neighbors) {
        return dataset.stream()
            .sorted((p, q) -> Double.compare(metric.eval(target, p), metric.eval(target, q)))
            .limit(neighbors)
            .collect(Collectors.toList());
    }

    private static <T> double maxDist(Collection<T> dataset, Metric<T> metric, T target) {
        return dataset.stream()
            .mapToDouble(p -> metric.eval(target, p))
            .max()
            .orElse(Double.POSITIVE_INFINITY);
    }

    public static <T> List<T> ballSearch(Collection<T> dataset, Metric<T> metric, T target, double radius) {
        return dataset.stream()
            .filter(p -> metric.eval(target, p) <= radius)
            .collect(Collectors.toList());
    }

    public static <T> void testKNNSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, int neighbors) {
        for (T target : dataset) {
            Collection<T> results = vpTree.knnSearch(target, neighbors);
            Collection<T> expected = knnSearch(dataset, metric, target, neighbors);
            double maxDist = maxDist(results, metric, target);
            double maxDistExpected = maxDist(expected, metric, target);
            assertEquals(neighbors, results.size());
            assertEquals(maxDistExpected, maxDist, 0.0000001);
        }
    }

    public static <T> void testBallSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, double radius) {
        for (T point : dataset) {
            Collection<T> res = vpTree.ballSearch(point, radius);
            Collection<T> expected = ballSearch(dataset, metric, point, radius);
            assertTrue(res.contains(point));
            assertEquals(new HashSet<>(expected), new HashSet<>(res));
        }
    }

}
