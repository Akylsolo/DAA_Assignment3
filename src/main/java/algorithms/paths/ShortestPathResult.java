package algorithms.paths;

import metrics.OperationMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of single-pass DAG shortest path computation.
 */
public class ShortestPathResult {
    private final long[] distances;
    private final int[] parent;
    private final OperationMetrics metrics;

    public ShortestPathResult(long[] distances, int[] parent, OperationMetrics metrics) {
        this.distances = distances;
        this.parent = parent;
        this.metrics = metrics;
    }

    public long[] getDistances() {
        return distances.clone();
    }

    public long getDistance(int vertex) {
        return distances[vertex];
    }

    public int getParent(int vertex) {
        return parent[vertex];
    }

    public long getSumOfDistances() {
        long sum = 0;
        for (long d : distances) {
            sum += d;
        }
        return sum;
    }

    /**
     * Reconstructs the shortest path from the virtual super-source to target.
     */
    public List<Integer> getPath(int target) {
        if (distances[target] >= Long.MAX_VALUE / 4) {
            return Collections.emptyList(); // unreachable
        }
        List<Integer> path = new ArrayList<>();
        for (int curr = target; curr != -1; curr = parent[curr]) {
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }

    public OperationMetrics getMetrics() {
        return metrics;
    }
}
