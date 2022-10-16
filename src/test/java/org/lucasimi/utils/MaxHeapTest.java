package org.lucasimi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;

public class MaxHeapTest {

    @Test
    public void testMax() {
        MaxHeap<Integer> heap = new MaxHeap<>((x, y) -> Integer.compare(x, y), 1);
        Collection<Integer> points = DatasetGenerator.linearDataset(20);
        assertTrue(heap.isEmpty());
        heap.addAll(points, 10);
        assertEquals(10, heap.size());
        for (int i = 9; i > -1; i--) {
            Integer max = heap.extractMax().orElse(null);
            assertEquals(Integer.valueOf(i), max);
            assertEquals(i, heap.size());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testIdentical() {
        MaxHeap<Integer> heap = new MaxHeap<>((x, y) -> Integer.compare(x, y), 1);
        Collection<Integer> points = DatasetGenerator.linearDataset(20);
        assertTrue(heap.isEmpty());
        heap.addAll(points);
        assertEquals(20, heap.size());
    }

}
