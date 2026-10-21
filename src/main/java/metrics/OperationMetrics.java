package metrics;

/**
 * Tracks reproducible operation counters inside algorithms:
 * - dsuOps: find + union operations in DSU
 * - edgeScans: edges inspected during graph traversal or edge iteration
 * - comparisons: edge weights compared (sorting, heaps)
 * - maxDepth: maximum depth of explicit stacks during iterative traversals
 */
public class OperationMetrics {
    private long dsuOps;
    private long edgeScans;
    private long comparisons;
    private int maxDepth;

    public OperationMetrics() {
        reset();
    }

    public void reset() {
        this.dsuOps = 0;
        this.edgeScans = 0;
        this.comparisons = 0;
        this.maxDepth = 0;
    }

    public void incDsuOps() {
        this.dsuOps++;
    }

    public void addDsuOps(long count) {
        this.dsuOps += count;
    }

    public void incEdgeScans() {
        this.edgeScans++;
    }

    public void addEdgeScans(long count) {
        this.edgeScans += count;
    }

    public void incComparisons() {
        this.comparisons++;
    }

    public void addComparisons(long count) {
        this.comparisons += count;
    }

    public void updateDepth(int currentDepth) {
        if (currentDepth > this.maxDepth) {
            this.maxDepth = currentDepth;
        }
    }

    public long getDsuOps() {
        return dsuOps;
    }

    public long getEdgeScans() {
        return edgeScans;
    }

    public long getComparisons() {
        return comparisons;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public String toString() {
        return String.format("OperationMetrics{dsuOps=%d, edgeScans=%d, comparisons=%d, maxDepth=%d}",
                dsuOps, edgeScans, comparisons, maxDepth);
    }
}
