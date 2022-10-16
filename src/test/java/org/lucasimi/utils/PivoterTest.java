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

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(PivoterTest.class);

    public void testPivotLinearity() {
        Random rand = new Random();
        for (int k = 0; k < 6; k++) {
            int size = (int) Math.pow(10, k);
            List<Integer> dataset = DatasetGenerator.linearDataset(size);
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
        int times = 1000;
        for (int i = 0; i < times; i++) {
            List<Integer> array = DatasetGenerator.randomDataset(1000, 1, 500);
            Pivoter.quickSelect(array, 0, array.size(), 0);
            Integer foundMin = array.get(0);
            assertEquals(findMin(array), foundMin);
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
