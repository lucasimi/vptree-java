package org.lucasimi.vptree.split;

import org.lucasimi.vptree.search.SearchAlgorithm;

public class SplitNode<T> implements SplitTree<T> {

    private final T center;

    private final double radius;

    private final SplitTree<T> left;

    private final SplitTree<T> right;

    public SplitNode(T center, double radius, SplitTree<T> left, SplitTree<T> right) {
        this.center = center;
        this.radius = radius;
        this.left = left;
        this.right = right;
    }

    @Override
    public void search(SearchAlgorithm<T> searchAlgorithm) {
        searchAlgorithm.search(this);
    }

    public T getCenter() {
        return this.center;
    }

    public double getRadius() {
        return this.radius;
    }

    public SplitTree<T> getLeft() {
        return this.left;
    }

    public SplitTree<T> getRight() {
        return this.right;
    }

}
