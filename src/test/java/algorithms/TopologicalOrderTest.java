package algorithms;

import algorithms.topo.KahnTopologicalSort;
import algorithms.topo.TopologicalResult;
import graph.Graph;
import graph.GraphGenerator;
import graph.GraphReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalOrderTest {

    @Test
    @DisplayName("Kahn produces valid topological order on random DAGs")
    void testValidOrderOnDAGs() {
        Random rnd = new Random(33221);
        for (int trial = 0; trial < 50; trial++) {
            int n = 10 + rnd.nextInt(91); // 10 to 100
            int m = rnd.nextInt(n * 4 + 1);
            long seed = rnd.nextLong();

            Graph dag = GraphGenerator.generateDAG(n, m, seed);

            TopologicalResult result = KahnTopologicalSort.sort(dag);

            assertFalse(result.hasCycle(), "DAG must not be reported as having a cycle");
            assertEquals(n, result.getProcessedCount(), "All n vertices must be ordered");
            assertTrue(KahnTopologicalSort.validateOrder(dag, result.getOrder()),
                    "Order must satisfy pos[u] < pos[v] for every edge u -> v");
        }
    }

    @Test
    @DisplayName("Kahn detects cycle on cyclic graphs")
    void testCycleDetected() throws IOException {
        Graph cyclic = GraphReader.readFromFile(Paths.get("data", "graph_cyclic.txt"));
        TopologicalResult result = KahnTopologicalSort.sort(cyclic);

        assertTrue(result.hasCycle(), "Cycle must be detected in cyclic graph");
        assertTrue(result.getProcessedCount() < cyclic.getN(), "Fewer than n vertices must leave queue");
    }

    @Test
    @DisplayName("Kahn works on small DAG file data/graph_dag.txt")
    void testSampleDAG() throws IOException {
        Graph dag = GraphReader.readFromFile(Paths.get("data", "graph_dag.txt"));
        TopologicalResult result = KahnTopologicalSort.sort(dag);

        assertFalse(result.hasCycle());
        assertEquals(dag.getN(), result.getProcessedCount());
        assertTrue(KahnTopologicalSort.validateOrder(dag, result.getOrder()));
    }
}
