package graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GraphCoreTest {

    @Test
    @DisplayName("Graph handles directed and undirected configurations properly")
    void testGraphBasics() {
        Graph undir = new Graph(4, false);
        undir.addEdge(0, 1, 10);
        undir.addEdge(1, 2, 20);
        assertFalse(undir.isDirected());
        assertEquals(4, undir.getN());
        assertEquals(2, undir.getM());
        assertEquals(1, undir.getNeighbors(0).size());
        assertEquals(2, undir.getNeighbors(1).size());
        assertEquals(1, undir.getNeighbors(2).size());
        assertEquals(0, undir.getNeighbors(3).size());

        Graph dir = new Graph(3, true);
        dir.addEdge(0, 1, 5);
        dir.addEdge(1, 2, 15);
        assertTrue(dir.isDirected());
        assertEquals(1, dir.getNeighbors(0).size());
        assertEquals(1, dir.getNeighbors(1).size());
        assertEquals(0, dir.getNeighbors(2).size());

        Graph rev = dir.reverse();
        assertEquals(0, rev.getNeighbors(0).size());
        assertEquals(1, rev.getNeighbors(1).size());
        assertEquals(1, rev.getNeighbors(2).size());
    }

    @Test
    @DisplayName("Graph handles self loops, parallel edges, and isolated vertices")
    void testEdgeCases() {
        Graph g = new Graph(5, true);
        g.addEdge(0, 0, 100);
        g.addEdge(0, 1, 10);
        g.addEdge(0, 1, 20);
        assertEquals(3, g.getM());
        assertEquals(3, g.getNeighbors(0).size());

        int[] inDeg = g.computeInDegrees();
        assertEquals(1, inDeg[0]);
        assertEquals(2, inDeg[1]);
        assertEquals(0, inDeg[2]);
        assertEquals(0, inDeg[3]);
        assertEquals(0, inDeg[4]);

        int[] outDeg = g.computeOutDegrees();
        assertEquals(3, outDeg[0]);
        assertEquals(0, outDeg[1]);

        assertThrows(IndexOutOfBoundsException.class, () -> g.addEdge(-1, 0, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> g.addEdge(0, 5, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> g.getNeighbors(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> g.getNeighbors(5));
        assertThrows(IllegalArgumentException.class, () -> new Graph(-1, true));
    }

    @Test
    @DisplayName("Edge compareTo and equality contracts")
    void testEdgeContracts() {
        Edge e1 = new Edge(0, 1, 10);
        Edge e2 = new Edge(0, 1, 10);
        Edge e3 = new Edge(0, 1, 20);
        Edge e4 = new Edge(0, 2, 10);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, e3);
        assertTrue(e1.compareTo(e3) < 0);
        assertTrue(e1.compareTo(e4) < 0);
        assertNotNull(e1.toString());
    }

    @Test
    @DisplayName("GraphReader reads all data files without errors")
    void testGraphReader() throws IOException {
        Graph g1 = GraphReader.readFromFile(Paths.get("data", "graph_disconnected.txt"));
        assertNotNull(g1);
        assertEquals(6, g1.getN());
        assertEquals(4, g1.getM());
        assertFalse(g1.isDirected());

        Graph g2 = GraphReader.readFromFile(Paths.get("data", "graph_cyclic.txt"));
        assertNotNull(g2);
        assertEquals(5, g2.getN());
        assertEquals(5, g2.getM());
        assertTrue(g2.isDirected());

        Graph g3 = GraphReader.readFromFile(Paths.get("data", "graph_dag.txt"));
        assertNotNull(g3);
        assertEquals(6, g3.getN());
        assertEquals(7, g3.getM());
        assertTrue(g3.isDirected());
    }
}
