package org.lucasimi.vptree;

import java.util.Collection;

import org.lucasimi.utils.Metric;
import org.lucasimi.vptree.flat.FlatVPTree;
import org.lucasimi.vptree.split.SplitVPTree;

public interface VPTree<T> {

    public Collection<T> knnSearch(T target, int neighbors);

    public Collection<T> ballSearch(T target, double eps);

    public Collection<T> getCenters();

    public static enum TreeType {
        FLAT,
        SPLIT
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private int leafCapacity = 1;

        private double leafRadius = 0.0;

        private boolean randomPivoting = true;

        private TreeType treeType = TreeType.SPLIT;

        private Metric<T> metric;

        public Builder() {}

		public int getLeafCapacity() {
			return leafCapacity;
		}

		public double getLeafRadius() {
			return leafRadius;
		}

		public boolean isRandomPivoting() {
			return randomPivoting;
		}

		public TreeType getTreeType() {
			return treeType;
		}

		public Metric<T> getMetric() {
			return metric;
		}

        public Builder<T> withLeafCapacity(int leafCapacity) {
            this.leafCapacity = leafCapacity;
            return this;
        }

        public Builder<T> withLeafRadius(double leafRadius) {
            this.leafRadius = leafRadius;
            return this;
        }

        public Builder<T> withRandomPivoting(boolean randomPivoting) {
            this.randomPivoting = randomPivoting;
            return this;
        }

        public Builder<T> withMetric(Metric<T> metric) {
            this.metric = metric;
            return this;
        }

        public Builder<T> withTreeType(TreeType treeType) {
            this.treeType = treeType;
            return this;
        }

        public VPTree<T> build(Collection<T> data) {
            switch (this.treeType) {
                case FLAT:
                    return new FlatVPTree<>(this, data);
                case SPLIT:
                    return new SplitVPTree<>(this, data);
                default: 
                    throw new IllegalArgumentException("Unknown tree type: " + this.treeType);
            }
        }

    }

}

