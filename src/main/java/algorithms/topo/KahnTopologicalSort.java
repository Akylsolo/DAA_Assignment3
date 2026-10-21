package algorithms.topo;

import graph.Edge;
import graph.Graph;
import metrics.OperationMetrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class KahnTopologicalSort {

    public static class CycleDetectedException extends IllegalStateException {
        public CycleDetectedException(String message) {
            super(message);
        }
    }

    public static TopologicalResult sort(Graph graph) {
        return sort(graph, new OperationMetrics());
    }

    public static TopologicalResult sort(Graph graph, OperationMetrics metrics) {
        int n = graph.getN();
        int[] inDegree = graph.computeInDegrees();

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }
        metrics.updateDepth(queue.size());

        List<Integer> order = new ArrayList<>(n);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Edge edge : graph.getNeighbors(u)) {
                metrics.incEdgeScans();
                int v = edge.getV();
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                }
            }
            metrics.updateDepth(queue.size());
        }

        boolean hasCycle = (order.size() < n);
        return new TopologicalResult(hasCycle, order, metrics);
    }

    public static boolean validateOrder(Graph graph, List<Integer> order) {
        int n = graph.getN();
        if (order.size() != n) {
            return false;
        }

        int[] pos = new int[n];
        for (int i = 0; i < n; i++) {
            pos[order.get(i)] = i;
        }

        for (Edge edge : graph.getEdges()) {
            int u = edge.getU();
            int v = edge.getV();
            if (pos[u] >= pos[v]) {
                return false;
            }
        }
        return true;
    }
}
