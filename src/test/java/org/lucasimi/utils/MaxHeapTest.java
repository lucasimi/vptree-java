package org.lucasimi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.lucasimi.DatasetGenerator;

public class MaxHeapTest {

    @Test
    public void testSingleton() {
        MaxHeap<Integer> heap = new MaxHeap<>(1);
        assertEquals(0, heap.size());
        heap.add(10);
        assertEquals(1, heap.size());
        int max = heap.extractMax().orElse(null);
        assertEquals(10, max);
        assertEquals(0, heap.size());
    }

    @Test
    public void testMaxHeap() {
        MaxHeap<Integer> heap = new MaxHeap<>(1);
        int size = 10;
        for (int i = 0; i < size; i++) {
            heap.add(i);
        }
        assertEquals(size, heap.size());
        for (int i = size; i > 0; i--) {
            Integer max = heap.extractMax().orElse(null);
            assertEquals(Integer.valueOf(i - 1), max);
            assertEquals(i - 1, heap.size());
        }
    }

    @Test
    public void testMax() {
        MaxHeap<Integer> heap = new MaxHeap<>(10);
        Collection<Integer> points = DatasetGenerator.linear(20);
        assertTrue(heap.isEmpty());
        heap.addAll(points);
        assertEquals(20, heap.size());
        for (int i = 20; i > 0; i--) {
            Integer max = heap.extractMax().orElse(null);
            assertEquals(Integer.valueOf(i - 1), max);
            assertEquals(i - 1, heap.size());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testIdentical() {
        MaxHeap<Integer> heap = new MaxHeap<>(1);
        Collection<Integer> points = DatasetGenerator.linear(20);
        assertTrue(heap.isEmpty());
        heap.addAll(points);
        assertEquals(20, heap.size());
    }

    @Test 
    public void testRandom() {
        Random rand = new Random();
        int size = 100;
        MaxHeap<Integer> heap = new MaxHeap<>(size);
        List<Integer> data = new ArrayList<>(10 * size);
        for (int i = 0; i < (10 * size); i++) {
            Integer val = rand.nextInt(size);
            heap.add(val);
            data.add(val);
        }
        assertEquals(10 * size, heap.size());
        Integer max = Integer.MAX_VALUE;
        for (int i = 0; i < (10 * size); i++) {
            assertEquals(10 * size - i, heap.size());
            Integer val = heap.extractMax().get();
            assertTrue(val <= max);
            max = val;
        }
        assertEquals(0, heap.size());
    }

}
