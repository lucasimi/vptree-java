package org.lucasimi.vptree.search;

import java.util.Collection;
import java.util.stream.Collectors;

import org.lucasimi.utils.MaxHeap;
import org.lucasimi.utils.Metric;
import org.lucasimi.utils.Ordered;

public class KNNSearchResults<T> implements SearchResults<T> {

    private final Metric<T> metric;

    private final T target;

    private final int neighbors;

    private final MaxHeap<Ordered<Double, T>> points;

    public KNNSearchResults(Metric<T> metric, T target, int neighbors) {
        this.metric = metric;
        this.target = target;
        this.neighbors = neighbors;
        this.points = new MaxHeap<>(neighbors);
    }

    public void add(T data) {
        double dist = this.metric.eval(this.target, data);
        double radius = this.getRadius();
        if (dist <= radius) {
            this.points.add(new Ordered<>(dist, data));
            while (this.points.size() > neighbors) {
                this.points.extractMax();
            }
        }
    }

    public void addAll(Collection<T> data) {
        data.stream().forEach(this::add);
    }

    public double getRadius() {
        if (this.points.size() < this.neighbors) {
            return Double.POSITIVE_INFINITY;
        } else {
            return this.points.getMax()
                    .orElseThrow()
                    .getOrder();
        }
    }

    @Override
    public Collection<T> extractPoints() {
        return this.points.extractAll().stream()
            .map(Ordered::getData)
            .collect(Collectors.toList());
    }

	public Metric<T> getMetric() {
		return metric;
	}

	public T getTarget() {
		return target;
	}

	public int getNeighbors() {
		return neighbors;
	}

}

