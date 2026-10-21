package algorithms.paths;

import algorithms.topo.KahnTopologicalSort;
import algorithms.topo.TopologicalResult;
import graph.Edge;
import graph.Graph;
import metrics.OperationMetrics;

import java.util.Arrays;
import java.util.List;

/**
 * Computes single-source / multi-source shortest paths on a DAG in O(V + E) time.
 * All in-degree 0 vertices start at distance 0 (acting as a virtual super-source).
 * Safely handles arbitrary edge weights (including negative weights).
 */
public class DagShortestPath {

    public static final long INF = Long.MAX_VALUE / 4;

    public static ShortestPathResult findShortestPaths(Graph graph) {
        return findShortestPaths(graph, new OperationMetrics());
    }

    public static ShortestPathResult findShortestPaths(Graph graph, OperationMetrics metrics) {
        TopologicalResult topoResult = KahnTopologicalSort.sort(graph);
        if (topoResult.hasCycle()) {
            throw new IllegalArgumentException("Cannot compute DAG shortest paths: graph contains a directed cycle.");
        }
        return findShortestPaths(graph, topoResult.getOrder(), metrics);
    }

    public static ShortestPathResult findShortestPaths(Graph graph, List<Integer> topoOrder, OperationMetrics metrics) {
        int n = graph.getN();
        int[] inDegree = graph.computeInDegrees();

        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        // Every vertex with in-degree 0 starts at distance 0 (virtual super-source)
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        // Single pass relaxation in topological order
        for (int u : topoOrder) {
            if (dist[u] >= INF) {
                continue;
            }
            for (Edge edge : graph.getNeighbors(u)) {
                metrics.incEdgeScans();
                int v = edge.getV();
                long newDist = dist[u] + edge.getWeight();
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    parent[v] = u;
                }
            }
        }

        return new ShortestPathResult(dist, parent, metrics);
    }
}
