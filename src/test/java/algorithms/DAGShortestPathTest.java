package algorithms;

import algorithms.paths.DagShortestPath;
import algorithms.paths.ShortestPathResult;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import graph.GraphReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {

    /**
     * Bellman-Ford reference implementation with virtual super-source connected to all 0-in-degree vertices with weight 0.
     */
    static long[] bellmanFordReference(Graph dag) {
        int n = dag.getN();
        int[] inDegree = dag.computeInDegrees();

        long[] dist = new long[n];
        Arrays.fill(dist, DagShortestPath.INF);

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        // Relax |V| - 1 times
        for (int iter = 0; iter < n - 1; iter++) {
            boolean changed = false;
            for (Edge e : dag.getEdges()) {
                if (dist[e.getU()] < DagShortestPath.INF) {
                    long candidate = dist[e.getU()] + e.getWeight();
                    if (candidate < dist[e.getV()]) {
                        dist[e.getV()] = candidate;
                        changed = true;
                    }
                }
            }
            if (!changed) break;
        }

        return dist;
    }

    @Test
    @DisplayName("DAG shortest paths agree with Bellman-Ford on random DAGs including negative weights")
    void testAgainstBellmanFord() {
        Random rnd = new Random(77889);
        for (int trial = 0; trial < 50; trial++) {
            int n = 10 + rnd.nextInt(41); // 10 to 50 vertices
            int m = rnd.nextInt(n * 4 + 1);

            // Generate DAG with potentially negative weights
            Graph dag = new Graph(n, true);
            int[] rank = new int[n];
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int tmp = rank[i]; rank[i] = rank[j]; rank[j] = tmp;
            }

            for (int i = 0; i < m; i++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                while (b == a) b = rnd.nextInt(n);
                if (rank[a] > rank[b]) {
                    int t = a; a = b; b = t;
                }
                // Weights from -50 to +100
                int w = -50 + rnd.nextInt(151);
                dag.addEdge(a, b, w);
            }

            long[] expected = bellmanFordReference(dag);
            ShortestPathResult actual = DagShortestPath.findShortestPaths(dag);

            for (int i = 0; i < n; i++) {
                assertEquals(expected[i], actual.getDistance(i),
                        String.format("Distance mismatch for vertex %d on trial %d", i, trial));
            }
        }
    }

    @Test
    @DisplayName("Path restoring recovers a valid shortest path matching target distance")
    void testPathRestoration() throws IOException {
        Graph dag = GraphReader.readFromFile(Paths.get("data", "graph_dag.txt"));
        ShortestPathResult result = DagShortestPath.findShortestPaths(dag);

        for (int v = 0; v < dag.getN(); v++) {
            if (result.getDistance(v) < DagShortestPath.INF) {
                List<Integer> path = result.getPath(v);
                assertFalse(path.isEmpty());
                assertEquals(v, path.get(path.size() - 1));

                // First vertex on path must be a super-source (in-degree 0)
                int start = path.get(0);
                assertEquals(0, dag.computeInDegrees()[start]);

                // Verify sum of edge weights along path matches distance
                long calculatedDist = 0;
                for (int i = 0; i < path.size() - 1; i++) {
                    int u = path.get(i);
                    int next = path.get(i + 1);
                    boolean edgeFound = false;
                    for (Edge e : dag.getNeighbors(u)) {
                        if (e.getV() == next) {
                            calculatedDist += e.getWeight();
                            edgeFound = true;
                            break;
                        }
                    }
                    assertTrue(edgeFound, "Every step on path must be an actual edge in the graph");
                }
                assertEquals(result.getDistance(v), calculatedDist, "Path weight sum must equal computed distance");
            }
        }
    }
}
