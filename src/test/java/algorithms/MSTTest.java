package algorithms;

import algorithms.mst.KruskalMST;
import algorithms.mst.MSTResult;
import algorithms.mst.PrimMST;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import graph.GraphReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    @DisplayName("Kruskal and Prim return equal total weight and n - 1 edges on 100 random connected graphs")
    void test100RandomConnectedGraphs() {
        Random rnd = new Random(9876);
        for (int t = 0; t < 100; t++) {
            int n = 10 + rnd.nextInt(41);
            int m = n - 1 + rnd.nextInt(150);
            long seed = rnd.nextLong();

            Graph graph = GraphGenerator.generateUndirected(n, m, seed);

            MSTResult kruskal = KruskalMST.findMST(graph);
            MSTResult prim = PrimMST.findMST(graph);

            assertEquals(kruskal.getTotalWeight(), prim.getTotalWeight(),
                    String.format("Weights must match on trial %d (n=%d, m=%d)", t, n, m));
            assertEquals(1, kruskal.getComponents(), "Connected graph must have 1 component");
            assertEquals(1, prim.getComponents(), "Connected graph must have 1 component");
            assertEquals(n - 1, kruskal.getEdges().size(), "Tree must have n - 1 edges");
            assertEquals(n - 1, prim.getEdges().size(), "Tree must have n - 1 edges");
        }
    }

    @Test
    @DisplayName("MST algorithms handle disconnected graphs by returning a spanning forest")
    void testDisconnectedGraph() throws IOException {
        Graph graph = GraphReader.readFromFile(Paths.get("data", "graph_disconnected.txt"));
        assertEquals(6, graph.getN());

        MSTResult kruskal = KruskalMST.findMST(graph);
        MSTResult prim = PrimMST.findMST(graph);

        assertEquals(kruskal.getTotalWeight(), prim.getTotalWeight());

        assertEquals(3, kruskal.getComponents());
        assertEquals(3, prim.getComponents());

        assertEquals(3, kruskal.getEdges().size());
        assertEquals(3, prim.getEdges().size());
    }

    @Test
    @DisplayName("MST algorithms handle parallel edges and self loops safely")
    void testParallelEdgesAndSelfLoops() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 8);
        graph.addEdge(2, 3, 12);
        graph.addEdge(0, 0, 1);
        graph.addEdge(3, 3, 2);

        MSTResult kruskal = KruskalMST.findMST(graph);
        MSTResult prim = PrimMST.findMST(graph);

        assertEquals(kruskal.getTotalWeight(), prim.getTotalWeight());
        assertEquals(5 + 8 + 12, kruskal.getTotalWeight());
        assertEquals(3, kruskal.getEdges().size());
        assertEquals(3, prim.getEdges().size());
    }
}
