package algorithms;

import algorithms.scc.KosarajuSCC;
import algorithms.scc.SCCResult;
import algorithms.scc.TarjanSCC;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import graph.GraphReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SCCTest {

    /**
     * Brute force SCC: u and v are in same SCC iff u can reach v AND v can reach u.
     */
    static List<Set<Integer>> bruteForceSCC(Graph graph) {
        int n = graph.getN();
        boolean[][] reachable = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            boolean[] visited = new boolean[n];
            Queue<Integer> q = new ArrayDeque<>();
            q.add(i);
            visited[i] = true;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (Edge e : graph.getNeighbors(u)) {
                    if (!visited[e.getV()]) {
                        visited[e.getV()] = true;
                        q.add(e.getV());
                    }
                }
            }
            reachable[i] = visited;
        }

        boolean[] assigned = new boolean[n];
        List<Set<Integer>> components = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!assigned[i]) {
                Set<Integer> comp = new HashSet<>();
                comp.add(i);
                assigned[i] = true;
                for (int j = i + 1; j < n; j++) {
                    if (!assigned[j] && reachable[i][j] && reachable[j][i]) {
                        comp.add(j);
                        assigned[j] = true;
                    }
                }
                components.add(comp);
            }
        }
        return components;
    }

    private void assertSameComponents(List<Set<Integer>> expected, SCCResult actual, int n) {
        assertEquals(expected.size(), actual.getComponentCount(), "Number of components must match");

        // Map actual components to Sets
        List<Set<Integer>> actualSets = new ArrayList<>();
        for (List<Integer> comp : actual.getComponents()) {
            actualSets.add(new HashSet<>(comp));
        }

        // Sort both by min element for direct comparison
        Comparator<Set<Integer>> compComparator = Comparator.comparingInt(s -> s.stream().min(Integer::compare).orElse(0));
        expected.sort(compComparator);
        actualSets.sort(compComparator);

        assertEquals(expected, actualSets, "Components partition must be identical");

        // Verify componentId mapping
        for (Set<Integer> comp : actualSets) {
            int id = -1;
            for (int v : comp) {
                if (id == -1) {
                    id = actual.getComponentId(v);
                } else {
                    assertEquals(id, actual.getComponentId(v), "All vertices in same SCC must share componentId");
                }
            }
        }
    }

    @Test
    @DisplayName("Kosaraju and Tarjan agree with brute force on random graphs (n <= 200)")
    void testAgainstBruteForce() {
        Random rnd = new Random(54321);
        for (int trial = 0; trial < 20; trial++) {
            int n = 20 + rnd.nextInt(81); // 20 to 100 vertices
            int m = rnd.nextInt(n * 5 + 1); // varying density
            long seed = rnd.nextLong();

            Graph graph = GraphGenerator.generateDirected(n, m, seed);

            List<Set<Integer>> expected = bruteForceSCC(graph);
            SCCResult kosaraju = KosarajuSCC.findSCC(graph);
            SCCResult tarjan = TarjanSCC.findSCC(graph);

            assertSameComponents(expected, kosaraju, n);
            assertSameComponents(expected, tarjan, n);
        }
    }

    @Test
    @DisplayName("Stack Safety: SCC on a path of 100 000 vertices finishes without StackOverflowError")
    void testStackSafety100kPath() {
        int n = 100_000;
        Graph pathGraph = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            pathGraph.addEdge(i, i + 1, 1);
        }

        assertDoesNotThrow(() -> {
            SCCResult result = KosarajuSCC.findSCC(pathGraph);
            assertEquals(n, result.getComponentCount(), "Each vertex on a DAG path is its own SCC");
            assertTrue(result.getMetrics().getMaxDepth() >= n, "Explicit stack depth should reach n");
        });

        assertDoesNotThrow(() -> {
            SCCResult result = TarjanSCC.findSCC(pathGraph);
            assertEquals(n, result.getComponentCount());
            assertTrue(result.getMetrics().getMaxDepth() >= n);
        });
    }

    @Test
    @DisplayName("SCC correctly identifies cycles on small sample graph")
    void testCyclicGraphSample() throws IOException {
        Graph graph = GraphReader.readFromFile(Paths.get("data", "graph_cyclic.txt"));
        assertEquals(5, graph.getN());

        // In data/graph_cyclic.txt:
        // 0 -> 1 -> 2 -> 0 is an SCC of size 3 {0, 1, 2}
        // 2 -> 3, 3 -> 4: 3 and 4 are individual SCCs {3}, {4}
        SCCResult kosaraju = KosarajuSCC.findSCC(graph);
        SCCResult tarjan = TarjanSCC.findSCC(graph);

        assertEquals(3, kosaraju.getComponentCount());
        assertEquals(3, tarjan.getComponentCount());
        assertEquals(kosaraju.getComponentId(0), kosaraju.getComponentId(1));
        assertEquals(kosaraju.getComponentId(1), kosaraju.getComponentId(2));
        assertNotEquals(kosaraju.getComponentId(0), kosaraju.getComponentId(3));
        assertNotEquals(kosaraju.getComponentId(3), kosaraju.getComponentId(4));
    }
}
