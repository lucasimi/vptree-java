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
        List<T> arr = new ArrayList<>(dataset);
        return arr.stream()
            .sorted((p, q) -> Double.compare(metric.eval(target, p), metric.eval(target, q)))
            .limit(neighbors)
            .collect(Collectors.toList());
    }

    private static <T> double maxDist(Collection<T> dataset, Metric<T> metric, T target) {
        double maxDist = Double.MIN_VALUE;
        for (T point : dataset) {
            double dist = metric.eval(point, target);
            if (dist > maxDist) {
                maxDist = dist;
            }
        }
        return maxDist;
    }

    public static <T> List<T> ballSearch(Collection<T> dataset, Metric<T> metric, T target, double radius) {
        List<T> arr = new ArrayList<>(dataset);
        return arr.stream()
            .filter(p -> metric.eval(target, p) <= radius)
            .collect(Collectors.toList());
    }

    public static <T> void testKNNSearch(Collection<T> dataset, Metric<T> metric, VPTree<T> vpTree, int neighbors) {
        for (T target : dataset) {
            Collection<T> res = vpTree.knnSearch(target, neighbors);
            assertEquals(neighbors, res.size());
            double maxDist = maxDist(res, metric, target);
            for (T point : dataset) {
                if (!res.contains(point)) {
                    assertTrue(metric.eval(point, target) >= maxDist);
                }
            }
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
