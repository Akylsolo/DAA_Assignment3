package algorithms.scc;

import graph.Edge;
import graph.Graph;
import metrics.OperationMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Kosaraju's two-pass algorithm for Strongly Connected Components.
 * Uses an explicit iterative stack to guarantee survival at n = 100 000 vertices.
 * First pass: records finish order on the original graph.
 * Second pass: traverses the reversed graph in decreasing finish order to extract SCCs.
 */
public class KosarajuSCC {

    public static SCCResult findSCC(Graph graph) {
        return findSCC(graph, new OperationMetrics());
    }

    public static SCCResult findSCC(Graph graph, OperationMetrics metrics) {
        int n = graph.getN();
        if (n == 0) {
            return new SCCResult(0, new int[0], new ArrayList<>(), metrics);
        }

        boolean[] visited = new boolean[n];
        int[] finishOrder = new int[n];
        int[] orderIdx = new int[]{0};

        // Explicit stack structures for Iterative DFS Pass 1
        int[] stack = new int[n];
        int[] edgeIdx = new int[n];

        // Pass 1: compute finish order on original graph
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                int top = 0;
                stack[top] = i;
                visited[i] = true;
                edgeIdx[i] = 0;
                metrics.updateDepth(top + 1);

                while (top >= 0) {
                    int u = stack[top];
                    List<Edge> neighbors = graph.getNeighbors(u);
                    if (edgeIdx[u] < neighbors.size()) {
                        Edge edge = neighbors.get(edgeIdx[u]++);
                        metrics.incEdgeScans();
                        int v = edge.getV();
                        if (!visited[v]) {
                            visited[v] = true;
                            edgeIdx[v] = 0;
                            top++;
                            stack[top] = v;
                            metrics.updateDepth(top + 1);
                        }
                    } else {
                        finishOrder[orderIdx[0]++] = u;
                        top--;
                    }
                }
            }
        }

        // Pass 2: Traverse reversed graph in reverse finish order
        Graph revGraph = graph.reverse();
        Arrays.fill(visited, false);
        int[] componentId = new int[n];
        Arrays.fill(componentId, -1);
        List<List<Integer>> componentsList = new ArrayList<>();
        int componentCount = 0;

        for (int i = n - 1; i >= 0; i--) {
            int root = finishOrder[i];
            if (!visited[root]) {
                List<Integer> currentComponent = new ArrayList<>();
                int currentId = componentCount++;

                int top = 0;
                stack[top] = root;
                visited[root] = true;
                componentId[root] = currentId;
                currentComponent.add(root);
                edgeIdx[root] = 0;
                metrics.updateDepth(top + 1);

                while (top >= 0) {
                    int u = stack[top];
                    List<Edge> neighbors = revGraph.getNeighbors(u);
                    if (edgeIdx[u] < neighbors.size()) {
                        Edge edge = neighbors.get(edgeIdx[u]++);
                        metrics.incEdgeScans();
                        int v = edge.getV();
                        if (!visited[v]) {
                            visited[v] = true;
                            componentId[v] = currentId;
                            currentComponent.add(v);
                            edgeIdx[v] = 0;
                            top++;
                            stack[top] = v;
                            metrics.updateDepth(top + 1);
                        }
                    } else {
                        top--;
                    }
                }
                componentsList.add(currentComponent);
            }
        }

        return new SCCResult(componentCount, componentId, componentsList, metrics);
    }
}
