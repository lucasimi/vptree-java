package org.lucasimi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;

public class PivoterTest {

    private static final Random RAND = new Random();

    private static final int TIMES = 1000;

    private static final int SIZE = 1000;

    public void testPivotLinearity() {
        Random rand = new Random();
        for (int k = 0; k < 6; k++) {
            int size = (int) Math.pow(10, k);
            List<Integer> dataset = new ArrayList<>(DatasetGenerator.linear(size));
            int order = rand.ints(0, dataset.size()).findFirst().getAsInt();
            Pivoter.quickSelect(dataset, 0, dataset.size(), order);
            for (int i = 0; i < order; i++) {
                assertTrue(dataset.get(i) <= dataset.get(order));
            }
            for (int i = order; i < dataset.size(); i++) {
                assertTrue(dataset.get(i) >= dataset.get(order));
            }
        }
    }

    @Test
    public void testMin() {
        List<Integer> array = new ArrayList<>();
        array.add(1);
        array.add(0);
        Pivoter.quickSelect(array, 0, array.size(), 0);
        Integer foundMin = array.get(0);
        assertEquals(findMin(array), foundMin);
    }

    @Test
    public void testMinRandom() {
        for (int i = 0; i < TIMES; i++) {
            List<Integer> array = new ArrayList<>(DatasetGenerator.random(SIZE, 1, SIZE / 2));
            Pivoter.quickSelect(array, 0, array.size(), 0);
            Integer foundMin = array.get(0);
            assertEquals(findMin(array), foundMin);
        }
    }

    @Test
    public void testPartitionRandom() {
        for (int i = 0; i < TIMES; i++) {
            List<Integer> array = new ArrayList<>(DatasetGenerator.random(SIZE, 1, SIZE / 2));
            int k = RAND.nextInt(array.size());
            Integer pivot = array.get(k);
            int h = Pivoter.partition(array, 0, array.size(), k);
            for (int j = 0; j < h - 1; j++) {
                assertTrue(array.get(j) <= pivot);
            }
            assertEquals(pivot, array.get(h - 1));
            for (int j = h; j < array.size(); j++) {
                assertTrue(array.get(j) > pivot);
            }
        }
    }

    @Test
    public void testQuickSelectRandom() {
        for (int i = 0; i < TIMES; i++) {
            List<Integer> array = new ArrayList<>(DatasetGenerator.random(SIZE, 1, SIZE / 2));
            int k = RAND.nextInt(array.size());
            Pivoter.quickSelect(array, 0, array.size(), k);
            Integer pivot = array.get(k);
            for (int j = 0; j < k; j++) {
                assertTrue(array.get(j) <= pivot);
            }
            for (int j = k; j < array.size(); j++) {
                assertTrue(array.get(j) >= pivot);
            }
        }
    }

    private <T extends Comparable<T>> T findMin(Collection<T> array) {
        T bestPoint = null;
        for (T point : array) {
            if ((bestPoint == null) || (point.compareTo(bestPoint) < 0)) {
                bestPoint = point;
            }
        }
        return bestPoint;
    }

}
