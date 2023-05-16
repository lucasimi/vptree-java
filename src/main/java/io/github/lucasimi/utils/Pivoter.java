package io.github.lucasimi.utils;

import java.util.List;

public class Pivoter {

    private Pivoter() {
    }

    private static final <T> void swap(List<T> data, int i, int j) {
        T xi = data.get(i);
        T xj = data.get(j);
        data.set(i, xj);
        data.set(j, xi);
    }

    public static final <T extends Comparable<T>> int partition(T pivot, List<T> data, int start, int end) {
        int higher = start;
        for (int j = start; j < end; j++) {
            T curr = data.get(j);
            if (pivot.compareTo(curr) > 0) {
                swap(data, higher, j);
                higher += 1;
            }
        }
        return higher;
    }

    public static final <T extends Comparable<T>> void quickSelect(List<T> data, int start, int end, int k) {
        int start_ = start;
        int end_ = end;
        Integer higher = -1;
        while (higher != k + 1) {
            T pivot = data.get(k);
            swap(data, start_, k);
            higher = partition(pivot, data, start_ + 1, end_);
            swap(data, start_, higher - 1);
            if (k <= higher - 1) {
                end_ = higher;
            } else {
                start_ = higher;
            }
        }
    }

}
