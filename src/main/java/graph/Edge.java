package graph;

import java.util.Objects;

/**
 * Represents a weighted directed edge from u to v with an integer weight w.
 */
public class Edge implements Comparable<Edge> {
    private final int u;
    private final int v;
    private final int weight;

    public Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Edge o) {
        int cmp = Integer.compare(this.weight, o.weight);
        if (cmp != 0) return cmp;
        cmp = Integer.compare(this.u, o.u);
        if (cmp != 0) return cmp;
        return Integer.compare(this.v, o.v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return u == edge.u && v == edge.v && weight == edge.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v, weight);
    }

    @Override
    public String toString() {
        return String.format("(%d -> %d, w=%d)", u, v, weight);
    }
}
