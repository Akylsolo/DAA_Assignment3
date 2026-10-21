package graph;

import java.util.Random;

public class GraphGenerator {

    public static Graph generateUndirected(int n, int m, long seed) {
        if (m < n - 1) {
            throw new IllegalArgumentException("For connected graph, m must be >= n - 1 (got n=" + n + ", m=" + m + ")");
        }
        Random rnd = new Random(seed);
        Graph graph = new Graph(n, false);

        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }

        for (int i = 1; i < n; i++) {
            int u = perm[rnd.nextInt(i)];
            int v = perm[i];
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(u, v, w);
        }

        while (graph.getM() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, w);
        }

        return graph;
    }

    public static Graph generateDirected(int n, int m, long seed) {
        Random rnd = new Random(seed);
        Graph graph = new Graph(n, true);

        while (graph.getM() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, w);
        }

        return graph;
    }

    public static Graph generateDAG(int n, int m, long seed) {
        Random rnd = new Random(seed);
        Graph graph = new Graph(n, true);

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = rank[i];
            rank[i] = rank[j];
            rank[j] = tmp;
        }

        while (graph.getM() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            int w = 1 + rnd.nextInt(1000);
            if (rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            graph.addEdge(a, b, w);
        }

        return graph;
    }
}
