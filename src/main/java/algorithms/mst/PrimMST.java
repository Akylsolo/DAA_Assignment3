package algorithms.mst;

import graph.Edge;
import graph.Graph;
import metrics.OperationMetrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Prim's algorithm for finding Minimum Spanning Tree / Forest.
 * Uses a PriorityQueue of candidate edges.
 * Grows tree from vertex 0, and continues to remaining components if disconnected.
 */
public class PrimMST {

    public static MSTResult findMST(Graph graph) {
        return findMST(graph, new OperationMetrics());
    }

    public static MSTResult findMST(Graph graph, OperationMetrics metrics) {
        int n = graph.getN();
        if (n == 0) {
            return new MSTResult(new ArrayList<>(), 0, 0, metrics);
        }

        boolean[] inTree = new boolean[n];
        List<Edge> mstEdges = new ArrayList<>(Math.max(0, n - 1));
        long totalWeight = 0;
        int components = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2) -> {
            metrics.incComparisons();
            int cmp = Integer.compare(e1.getWeight(), e2.getWeight());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(e1.getU(), e2.getU());
            if (cmp != 0) return cmp;
            return Integer.compare(e1.getV(), e2.getV());
        });

        for (int start = 0; start < n; start++) {
            if (!inTree[start]) {
                components++;
                inTree[start] = true;

                for (Edge e : graph.getNeighbors(start)) {
                    metrics.incEdgeScans();
                    pq.offer(e);
                }
                metrics.updateDepth(pq.size());

                while (!pq.isEmpty()) {
                    Edge minEdge = pq.poll();
                    int to = minEdge.getV();

                    // Skip vertex already in tree
                    if (inTree[to]) {
                        continue;
                    }

                    inTree[to] = true;
                    mstEdges.add(minEdge);
                    totalWeight += minEdge.getWeight();

                    for (Edge e : graph.getNeighbors(to)) {
                        metrics.incEdgeScans();
                        if (!inTree[e.getV()]) {
                            pq.offer(e);
                        }
                    }
                    metrics.updateDepth(pq.size());
                }
            }
        }

        return new MSTResult(mstEdges, totalWeight, components, metrics);
    }
}
