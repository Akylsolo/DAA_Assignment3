package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of vertices cannot be negative: " + n);
        }
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            this.adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight) {
        checkVertex(u);
        checkVertex(v);
        Edge edge = new Edge(u, v, weight);
        edges.add(edge);
        adj.get(u).add(edge);
        if (!directed && u != v) {
            adj.get(v).add(new Edge(v, u, weight));
        } else if (!directed && u == v) {

        }
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= n) {
            throw new IndexOutOfBoundsException("Vertex index out of range [0, " + (n - 1) + "]: " + v);
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return edges.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> getNeighbors(int u) {
        checkVertex(u);
        return Collections.unmodifiableList(adj.get(u));
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Graph reverse() {
        if (!directed) {
            return this;
        }
        Graph rev = new Graph(n, true);
        for (Edge e : edges) {
            rev.addEdge(e.getV(), e.getU(), e.getWeight());
        }
        return rev;
    }

    public int[] computeInDegrees() {
        int[] inDegree = new int[n];
        for (List<Edge> neighborList : adj) {
            for (Edge e : neighborList) {

                if (directed) {
                    inDegree[e.getV()]++;
                }
            }
        }
        if (!directed) {
            for (int i = 0; i < n; i++) {
                inDegree[i] = adj.get(i).size();
            }
        }
        return inDegree;
    }

    public int[] computeOutDegrees() {
        int[] outDegree = new int[n];
        for (int i = 0; i < n; i++) {
            outDegree[i] = adj.get(i).size();
        }
        return outDegree;
    }
}
