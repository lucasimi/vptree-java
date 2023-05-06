package org.lucasimi.vptree.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lucasimi.utils.Metric;

public class BallSearchResults<T> implements SearchResults<T> {

    private final Metric<T> metric;

    private final T target;

    private final double eps;

    private final List<T> points;

    public BallSearchResults(Metric<T> metric, T target, double eps) {
        this.metric = metric;
        this.target = target;
        this.eps = eps;
        this.points = new ArrayList<>();
    }

    public BallSearchResults(Metric<T> metric, T target, double eps, int capa) {
        this.metric = metric;
        this.target = target;
        this.eps = eps;
        this.points = new ArrayList<>(capa);
    }

    public Metric<T> getMetric() {
        return this.metric;
    }

	public T getTarget() {
		return this.target;
	}

	public double getEps() {
		return this.eps;
	}

    @Override
    public Collection<T> getPoints() {
        return this.points;
    }

    public void add(T point) {
        this.points.add(point);
    }

}

