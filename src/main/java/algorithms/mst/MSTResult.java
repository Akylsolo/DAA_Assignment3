package algorithms.mst;

import graph.Edge;
import metrics.OperationMetrics;

import java.util.Collections;
import java.util.List;

public class MSTResult {
    private final List<Edge> edges;
    private final long totalWeight;
    private final int components;
    private final OperationMetrics metrics;

    public MSTResult(List<Edge> edges, long totalWeight, int components, OperationMetrics metrics) {
        this.edges = edges;
        this.totalWeight = totalWeight;
        this.components = components;
        this.metrics = metrics;
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public long getTotalWeight() {
        return totalWeight;
    }

    public int getComponents() {
        return components;
    }

    public OperationMetrics getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return String.format("MSTResult{totalWeight=%d, edges=%d, components=%d}",
                totalWeight, edges.size(), components);
    }
}
