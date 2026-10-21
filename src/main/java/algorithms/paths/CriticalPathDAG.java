package algorithms.paths;

import algorithms.scc.SCCResult;
import algorithms.topo.KahnTopologicalSort;
import algorithms.topo.TopologicalResult;
import graph.Edge;
import graph.Graph;

import java.util.*;

public class CriticalPathDAG {

    public static class CriticalPathResult {
        private final Graph condensationGraph;
        private final long criticalPathLength;
        private final List<Integer> criticalPath;

        public CriticalPathResult(Graph condensationGraph, long criticalPathLength, List<Integer> criticalPath) {
            this.condensationGraph = condensationGraph;
            this.criticalPathLength = criticalPathLength;
            this.criticalPath = criticalPath;
        }

        public Graph getCondensationGraph() {
            return condensationGraph;
        }

        public long getCriticalPathLength() {
            return criticalPathLength;
        }

        public List<Integer> getCriticalPath() {
            return Collections.unmodifiableList(criticalPath);
        }
    }

    public static Graph buildCondensationGraph(Graph original, SCCResult scc) {
        int compCount = scc.getComponentCount();
        Graph condensation = new Graph(compCount, true);

        Map<Long, Integer> maxEdgeWeight = new HashMap<>();

        for (Edge e : original.getEdges()) {
            int cu = scc.getComponentId(e.getU());
            int cv = scc.getComponentId(e.getV());
            if (cu != cv) {
                long key = (((long) cu) << 32) | (cv & 0xFFFFFFFFL);
                maxEdgeWeight.merge(key, e.getWeight(), Math::max);
            }
        }

        for (Map.Entry<Long, Integer> entry : maxEdgeWeight.entrySet()) {
            long key = entry.getKey();
            int cu = (int) (key >>> 32);
            int cv = (int) (key & 0xFFFFFFFFL);
            condensation.addEdge(cu, cv, entry.getValue());
        }

        return condensation;
    }

    public static CriticalPathResult findCriticalPath(Graph condensationDAG) {
        int n = condensationDAG.getN();
        if (n == 0) {
            return new CriticalPathResult(condensationDAG, 0, Collections.emptyList());
        }

        TopologicalResult topoResult = KahnTopologicalSort.sort(condensationDAG);
        if (topoResult.hasCycle()) {
            throw new IllegalStateException("Condensation graph must be an acyclic DAG, but a cycle was detected.");
        }
        List<Integer> topo = topoResult.getOrder();

        int[] inDegree = condensationDAG.computeInDegrees();
        long[] maxDist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(maxDist, Long.MIN_VALUE / 4);
        Arrays.fill(parent, -1);

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                maxDist[i] = 0;
            }
        }

        for (int u : topo) {
            if (maxDist[u] <= Long.MIN_VALUE / 8) continue;
            for (Edge e : condensationDAG.getNeighbors(u)) {
                int v = e.getV();
                long candidate = maxDist[u] + e.getWeight();
                if (candidate > maxDist[v]) {
                    maxDist[v] = candidate;
                    parent[v] = u;
                }
            }
        }

        long bestDist = 0;
        int bestEnd = -1;
        for (int i = 0; i < n; i++) {
            if (maxDist[i] > bestDist) {
                bestDist = maxDist[i];
                bestEnd = i;
            }
        }

        List<Integer> path = new ArrayList<>();
        if (bestEnd != -1) {
            for (int curr = bestEnd; curr != -1; curr = parent[curr]) {
                path.add(curr);
            }
            Collections.reverse(path);
        } else if (n > 0) {
            path.add(0);
        }

        return new CriticalPathResult(condensationDAG, bestDist, path);
    }
}
