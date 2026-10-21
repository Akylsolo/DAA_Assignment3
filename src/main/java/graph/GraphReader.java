package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

/**
 * Reads and writes Graph objects in standard text format:
 * First line: n m directed|undirected
 * Next m lines: u v w
 */
public class GraphReader {

    public static Graph readFromFile(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return read(reader);
        }
    }

    public static Graph readFromStream(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return read(reader);
        }
    }

    private static Graph read(BufferedReader reader) throws IOException {
        String line;
        StringTokenizer st = null;

        // Skip comments and empty lines until header
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            st = new StringTokenizer(line);
            break;
        }

        if (st == null) {
            throw new IllegalArgumentException("Empty graph file or missing header.");
        }

        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        String typeStr = st.nextToken().toLowerCase();
        boolean directed = typeStr.startsWith("dir");

        Graph graph = new Graph(n, directed);

        int loadedEdges = 0;
        while (loadedEdges < m && (line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            st = new StringTokenizer(line);
            int u = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            graph.addEdge(u, v, w);
            loadedEdges++;
        }

        return graph;
    }
}
