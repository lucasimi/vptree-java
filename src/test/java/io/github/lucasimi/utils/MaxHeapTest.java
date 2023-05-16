package io.github.lucasimi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import io.github.lucasimi.DatasetGenerator;

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
        Collection<Integer> points = DatasetGenerator.linearDataset(20);
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
        Collection<Integer> points = DatasetGenerator.linearDataset(20);
        assertTrue(heap.isEmpty());
        heap.addAll(points);
        assertEquals(20, heap.size());
    }

}
