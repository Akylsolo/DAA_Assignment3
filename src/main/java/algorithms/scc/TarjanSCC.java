package algorithms.scc;

import graph.Edge;
import graph.Graph;
import metrics.OperationMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TarjanSCC {

    public static SCCResult findSCC(Graph graph) {
        return findSCC(graph, new OperationMetrics());
    }

    public static SCCResult findSCC(Graph graph, OperationMetrics metrics) {
        int n = graph.getN();
        if (n == 0) {
            return new SCCResult(0, new int[0], new ArrayList<>(), metrics);
        }

        int[] index = new int[n];
        int[] lowlink = new int[n];
        boolean[] onStack = new boolean[n];
        Arrays.fill(index, -1);
        Arrays.fill(lowlink, -1);

        int[] activeStack = new int[n];
        int activeTop = 0;

        int[] callStack = new int[n];
        int[] edgeIdx = new int[n];

        int[] componentId = new int[n];
        Arrays.fill(componentId, -1);
        List<List<Integer>> componentsList = new ArrayList<>();
        int componentCount = 0;

        int timer = 0;

        for (int i = 0; i < n; i++) {
            if (index[i] == -1) {
                int callTop = 0;
                callStack[callTop] = i;
                index[i] = lowlink[i] = timer++;
                activeStack[activeTop++] = i;
                onStack[i] = true;
                edgeIdx[i] = 0;
                metrics.updateDepth(callTop + 1);

                while (callTop >= 0) {
                    int u = callStack[callTop];
                    List<Edge> neighbors = graph.getNeighbors(u);

                    if (edgeIdx[u] < neighbors.size()) {
                        Edge edge = neighbors.get(edgeIdx[u]++);
                        metrics.incEdgeScans();
                        int v = edge.getV();

                        if (index[v] == -1) {

                            index[v] = lowlink[v] = timer++;
                            activeStack[activeTop++] = v;
                            onStack[v] = true;
                            edgeIdx[v] = 0;
                            callTop++;
                            callStack[callTop] = v;
                            metrics.updateDepth(callTop + 1);
                        } else if (onStack[v]) {

                            lowlink[u] = Math.min(lowlink[u], index[v]);
                        }
                    } else {

                        if (lowlink[u] == index[u]) {
                            List<Integer> comp = new ArrayList<>();
                            int currentId = componentCount++;
                            while (activeTop > 0) {
                                int w = activeStack[--activeTop];
                                onStack[w] = false;
                                componentId[w] = currentId;
                                comp.add(w);
                                if (w == u) {
                                    break;
                                }
                            }
                            componentsList.add(comp);
                        }

                        callTop--;
                        if (callTop >= 0) {
                            int parent = callStack[callTop];
                            lowlink[parent] = Math.min(lowlink[parent], lowlink[u]);
                        }
                    }
                }
            }
        }

        return new SCCResult(componentCount, componentId, componentsList, metrics);
    }
}
