package algorithms.topo;

import metrics.OperationMetrics;

import java.util.Collections;
import java.util.List;

public class TopologicalResult {
    private final boolean hasCycle;
    private final List<Integer> order;
    private final OperationMetrics metrics;

    public TopologicalResult(boolean hasCycle, List<Integer> order, OperationMetrics metrics) {
        this.hasCycle = hasCycle;
        this.order = order;
        this.metrics = metrics;
    }

    public boolean hasCycle() {
        return hasCycle;
    }

    public List<Integer> getOrder() {
        return Collections.unmodifiableList(order);
    }

    public OperationMetrics getMetrics() {
        return metrics;
    }

    public int getProcessedCount() {
        return order.size();
    }

    @Override
    public String toString() {
        return hasCycle
                ? String.format("TopologicalResult{cycle detected, processed %d vertices}", order.size())
                : String.format("TopologicalResult{valid order with %d vertices}", order.size());
    }
}
