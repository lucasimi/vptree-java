package io.github.lucasimi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MaxHeap<T extends Comparable<T>> {

    private List<T> array;

    public MaxHeap(int capacity) {
        this.array = new ArrayList<>(capacity + 1);
    }

    public Optional<T> getMax() {
        if (this.array.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(array.get(0));
        }
    }

    public Optional<T> extractMax() {
        if (this.array.isEmpty()) {
            return Optional.empty();
        } else {
            T max = this.array.get(0);
            int lastIndex = this.array.size() - 1;
            this.array.set(0, this.array.get(lastIndex));
            this.array.remove(lastIndex);
            this.siftDown(0);
            return Optional.of(max);
        }
    }

    public void add(T value) {
        this.array.add(value);
        int node = this.array.size() - 1;
        siftUp(node);
    }

    private void siftUp(int index) {
        int node = index;
        boolean check = false;
        T nodeVal = this.array.get(node);
        while (!check) {
            int par = getParent(node);
            T parVal = this.array.get(par);
            if (parVal.compareTo(nodeVal) >= 0) {
                check = true;
            } else {
                this.array.set(par, nodeVal);
                this.array.set(node, parVal);
                node = par;
            }
        }
    }

    private void siftDown(int index) {
        if (index >= this.array.size()) {
            return;
        }
        int node = index;
        boolean check = false;
        T nodeVal = this.array.get(node);
        while (!check) {
            int max = node;
            T maxVal = nodeVal;
            int left = getLeft(node);
            if (left < this.array.size()) {
                T leftVal = this.array.get(left);
                if (maxVal.compareTo(leftVal) < 0) {
                    max = left;
                    maxVal = leftVal;
                }
            }
            int right = getRight(node);
            if (right < this.array.size()) {
                T rightVal = this.array.get(right);
                if (maxVal.compareTo(rightVal) < 0) {
                    max = right;
                    maxVal = rightVal;
                }
            }
            check = max == node;
            if (!check) {
                this.array.set(node, maxVal);
                this.array.set(max, nodeVal);
                node = max;
            }
        }
    }

    public void addAll(Collection<T> values) {
        for (T value : values) {
            this.add(value);
        }
    }

    private int getLeft(int i) {
        return 2 * i + 1;
    }

    private int getRight(int i) {
        return 2 * i + 2;
    }

    private int getParent(int i) {
        return (i - 1) / 2;
    }

    public int size() {
        return this.array.size();
    }

    public boolean isEmpty() {
        return this.array.isEmpty();
    }

}
