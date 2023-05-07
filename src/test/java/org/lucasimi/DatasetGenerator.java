package org.lucasimi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DatasetGenerator {

    private static final Random rand = new Random();

    private DatasetGenerator() {
    }

    public <T> Collection<T> empty() {
        return new ArrayList<>();
    }

    public static Collection<Integer> linear(int n) {
        List<Integer> dataset = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            dataset.add(i);
        }
        return dataset;
    }

    public static Collection<Integer> random(int size, int min, int bound) {
        List<Integer> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(min + rand.nextInt(bound - min));
        }
        return array;
    }

    public static Collection<double[]> random(int size, int dim, double min, double bound) {
        List<double[]> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            double[] point = new double[dim];
            for (int j = 0; j < dim; j++) {
                point[j] = min + (bound - min) * (rand.nextDouble());
            }
            array.add(point);
        }
        return array;
    }

}
