package algorithms;

import algorithms.paths.CriticalPathDAG;
import algorithms.scc.KosarajuSCC;
import algorithms.scc.SCCResult;
import algorithms.scc.TarjanSCC;
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

public class BonusTasksTest {

    @Test
    @DisplayName("Bonus Task A: Tarjan's SCC matches Kosaraju across various graphs")
    void testTarjanMatchesKosaraju() {
        Random rnd = new Random(998877);
        for (int i = 0; i < 30; i++) {
            int n = 15 + rnd.nextInt(50);
            int m = rnd.nextInt(n * 5 + 1);
            Graph g = GraphGenerator.generateDirected(n, m, rnd.nextLong());

            SCCResult kosaraju = KosarajuSCC.findSCC(g);
            SCCResult tarjan = TarjanSCC.findSCC(g);

            assertEquals(kosaraju.getComponentCount(), tarjan.getComponentCount());
            // Tarjan scans m edges, Kosaraju scans 2m edges
            assertEquals(m, tarjan.getMetrics().getEdgeScans());
            assertEquals(2L * m, kosaraju.getMetrics().getEdgeScans());
        }
    }

    @Test
    @DisplayName("Bonus Task B: Condensation Graph is strictly a DAG and Critical Path is computed")
    void testCondensationAndCriticalPath() throws IOException {
        Graph cyclic = GraphReader.readFromFile(Paths.get("data", "graph_cyclic.txt"));
        SCCResult scc = KosarajuSCC.findSCC(cyclic);

        Graph condensation = CriticalPathDAG.buildCondensationGraph(cyclic, scc);
        assertTrue(condensation.isDirected());
        assertEquals(scc.getComponentCount(), condensation.getN());

        // Check condensation graph has NO cycles (Kahn should order all vertices)
        TopologicalResult topo = KahnTopologicalSort.sort(condensation);
        assertFalse(topo.hasCycle(), "Condensation graph must be strictly a DAG");
        assertEquals(condensation.getN(), topo.getProcessedCount());

        CriticalPathDAG.CriticalPathResult cp = CriticalPathDAG.findCriticalPath(condensation);
        assertNotNull(cp.getCriticalPath());
        assertTrue(cp.getCriticalPathLength() >= 0);
    }
}
