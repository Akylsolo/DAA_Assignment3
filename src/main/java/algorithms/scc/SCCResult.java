package algorithms.scc;

import metrics.OperationMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of Strongly Connected Components decomposition.
 */
public class SCCResult {
    private final int componentCount;
    private final int[] componentId; // vertex -> component id (0 .. componentCount - 1)
    private final List<List<Integer>> components;
    private final OperationMetrics metrics;

    public SCCResult(int componentCount, int[] componentId, List<List<Integer>> components, OperationMetrics metrics) {
        this.componentCount = componentCount;
        this.componentId = componentId;
        this.components = components;
        this.metrics = metrics;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public int getComponentId(int vertex) {
        return componentId[vertex];
    }

    public int[] getComponentIds() {
        return componentId.clone();
    }

    public List<List<Integer>> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public OperationMetrics getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return String.format("SCCResult{components=%d}", componentCount);
    }
}
