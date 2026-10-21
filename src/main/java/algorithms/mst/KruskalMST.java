package algorithms.mst;

import algorithms.dsu.DisjointSetUnion;
import graph.Edge;
import graph.Graph;
import metrics.OperationMetrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KruskalMST {

    public static MSTResult findMST(Graph graph) {
        return findMST(graph, new OperationMetrics());
    }

    public static MSTResult findMST(Graph graph, OperationMetrics metrics) {
        int n = graph.getN();
        if (n == 0) {
            return new MSTResult(new ArrayList<>(), 0, 0, metrics);
        }

        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());

        sortedEdges.sort((e1, e2) -> {
            metrics.incComparisons();
            int cmp = Integer.compare(e1.getWeight(), e2.getWeight());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(e1.getU(), e2.getU());
            if (cmp != 0) return cmp;
            return Integer.compare(e1.getV(), e2.getV());
        });

        DisjointSetUnion dsu = new DisjointSetUnion(n, metrics);
        List<Edge> mstEdges = new ArrayList<>(Math.max(0, n - 1));
        long totalWeight = 0;

        for (Edge edge : sortedEdges) {
            metrics.incEdgeScans();
            int rootU = dsu.find(edge.getU());
            int rootV = dsu.find(edge.getV());
            if (rootU != rootV) {
                dsu.unionRoots(rootU, rootV);
                mstEdges.add(edge);
                totalWeight += edge.getWeight();

                if (mstEdges.size() == n - 1) {
                    break;
                }
            }
        }

        return new MSTResult(mstEdges, totalWeight, dsu.components(), metrics);
    }
}
