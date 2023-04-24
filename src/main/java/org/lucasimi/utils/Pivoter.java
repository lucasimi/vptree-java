package org.lucasimi.utils;

import java.util.List;

public class Pivoter {

    private Pivoter() {}

    private static final <T> void swap(List<T> data, int i, int j) {
        T xi = data.get(i);
        T xj = data.get(j);
        data.set(i, xj);
        data.set(j, xi);
    }

    public static final <T extends Comparable<T>> int partition(List<T> data, int start, int bound, int k) {
        if (k >= bound) {
            return bound;
        }
        if (k < start) {
            return start;
        }
        swap(data, k, start);
        T pivot = data.get(start);
        int higher = start + 1;
        for (int j = start + 1; j < bound; j++) {
            T curr = data.get(j);
            if (pivot.compareTo(curr) >= 0) {
                swap(data, higher, j);
                higher += 1;
            }
        }
        swap(data, start, higher - 1);
        return higher;
    }

    public static final <T extends Comparable<T>> void quickSelect(List<T> data, int start, int bound, int k) {
        if ((k >= bound) || (k < start)) {
            return;
        }
        int startTmp = start;
        int boundTmp = bound;
        while (true) {
            int higher = partition(data, startTmp, boundTmp, k);
            if (higher == k + 1) {
                return;
            } else if (higher > k + 1) {
                boundTmp = higher - 1;
            } else {
                startTmp = higher;
            }
        }
    }

}
