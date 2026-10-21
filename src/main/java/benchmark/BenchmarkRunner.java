package benchmark;

import algorithms.mst.KruskalMST;
import algorithms.mst.MSTResult;
import algorithms.mst.PrimMST;
import algorithms.paths.CriticalPathDAG;
import algorithms.paths.DagShortestPath;
import algorithms.paths.ShortestPathResult;
import algorithms.scc.KosarajuSCC;
import algorithms.scc.SCCResult;
import algorithms.scc.TarjanSCC;
import algorithms.topo.KahnTopologicalSort;
import algorithms.topo.TopologicalResult;
import graph.Graph;
import graph.GraphGenerator;
import metrics.OperationMetrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BenchmarkRunner {

    public static class GraphSpec {
        public final int n;
        public final int m;
        public final String density;
        public final long expectedMstWeight;
        public final int expectedSccCount;
        public final long expectedDagDistanceSum;

        public GraphSpec(int n, int m, String density, long expectedMstWeight, int expectedSccCount, long expectedDagDistanceSum) {
            this.n = n;
            this.m = m;
            this.density = density;
            this.expectedMstWeight = expectedMstWeight;
            this.expectedSccCount = expectedSccCount;
            this.expectedDagDistanceSum = expectedDagDistanceSum;
        }

        public String getGraphName() {
            return String.format("%d_%d_%s", n, m, density);
        }
    }

    public static final List<GraphSpec> BENCHMARK_GRAPHS = List.of(
            new GraphSpec(1_000, 4_000, "sparse", 147_970L, 27, 396_274L),
            new GraphSpec(1_000, 20_000, "dense", 30_363L, 1, 252_411L),
            new GraphSpec(3_162, 12_648, "sparse", 468_911L, 137, 1_271_911L),
            new GraphSpec(3_162, 63_240, "dense", 97_589L, 1, 723_157L),
            new GraphSpec(10_000, 40_000, "sparse", 1_486_139L, 373, 3_941_828L),
            new GraphSpec(10_000, 200_000, "dense", 309_550L, 1, 2_381_342L),
            new GraphSpec(31_623, 126_492, "sparse", 4_715_515L, 1_188, 12_551_891L),
            new GraphSpec(31_623, 632_460, "dense", 968_517L, 1, 7_376_612L),
            new GraphSpec(100_000, 400_000, "sparse", 15_012_094L, 3_923, 40_096_539L),
            new GraphSpec(100_000, 2_000_000, "dense", 3_056_681L, 1, 23_512_087L)
    );

    public static class Row {
        public String algorithm;
        public String graph;
        public int n;
        public int m;
        public double timeMs;
        public long dsuOps;
        public long edgeScans;
        public long comparisons;
        public int maxDepth;
        public long result;

        public String toCsv() {
            return String.format(Locale.US, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                    algorithm, graph, n, m, timeMs, dsuOps, edgeScans, comparisons, maxDepth, result);
        }
    }

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  DAA ASSIGNMENT 3: COMPREHENSIVE BENCHMARK & ALGORITHM VERIFICATION SUITE");
        System.out.println("===============================================================================");

        List<Row> rows = new ArrayList<>();
        final int NUM_RUNS = 5;

        for (GraphSpec spec : BENCHMARK_GRAPHS) {
            System.out.printf("\n>>> Evaluating Graph: n=%d, m=%d (%s) <<<\n", spec.n, spec.m, spec.density);

            System.out.println("  [Generating Undirected Graph...]");
            Graph undirected = GraphGenerator.generateUndirected(spec.n, spec.m, 42);

            System.out.print("  -> Running Kruskal... ");
            double[] kruskalTimes = new double[NUM_RUNS];
            OperationMetrics kruskalMetrics = null;
            MSTResult kruskalResult = null;
            for (int r = 0; r < NUM_RUNS; r++) {
                kruskalMetrics = new OperationMetrics();
                long start = System.nanoTime();
                kruskalResult = KruskalMST.findMST(undirected, kruskalMetrics);
                long elapsed = System.nanoTime() - start;
                kruskalTimes[r] = elapsed / 1_000_000.0;
            }
            Arrays.sort(kruskalTimes);
            double kruskalMedian = kruskalTimes[NUM_RUNS / 2];
            assertReference("Kruskal MST Weight", kruskalResult.getTotalWeight(), spec.expectedMstWeight);
            System.out.printf("OK (MST Weight: %d, Median Time: %.2f ms, Scans: %d)\n",
                    kruskalResult.getTotalWeight(), kruskalMedian, kruskalMetrics.getEdgeScans());

            Row kruskalRow = new Row();
            kruskalRow.algorithm = "Kruskal";
            kruskalRow.graph = spec.getGraphName();
            kruskalRow.n = spec.n;
            kruskalRow.m = spec.m;
            kruskalRow.timeMs = kruskalMedian;
            kruskalRow.dsuOps = kruskalMetrics.getDsuOps();
            kruskalRow.edgeScans = kruskalMetrics.getEdgeScans();
            kruskalRow.comparisons = kruskalMetrics.getComparisons();
            kruskalRow.maxDepth = kruskalMetrics.getMaxDepth();
            kruskalRow.result = kruskalResult.getTotalWeight();
            rows.add(kruskalRow);

            System.out.print("  -> Running Prim... ");
            double[] primTimes = new double[NUM_RUNS];
            OperationMetrics primMetrics = null;
            MSTResult primResult = null;
            for (int r = 0; r < NUM_RUNS; r++) {
                primMetrics = new OperationMetrics();
                long start = System.nanoTime();
                primResult = PrimMST.findMST(undirected, primMetrics);
                long elapsed = System.nanoTime() - start;
                primTimes[r] = elapsed / 1_000_000.0;
            }
            Arrays.sort(primTimes);
            double primMedian = primTimes[NUM_RUNS / 2];
            assertReference("Prim MST Weight", primResult.getTotalWeight(), spec.expectedMstWeight);
            if (primResult.getTotalWeight() != kruskalResult.getTotalWeight()) {
                throw new IllegalStateException("CRITICAL BUG: Kruskal and Prim total weights disagree! "
                        + kruskalResult.getTotalWeight() + " vs " + primResult.getTotalWeight());
            }
            System.out.printf("OK (MST Weight: %d, Median Time: %.2f ms, Agreement: MATCH)\n",
                    primResult.getTotalWeight(), primMedian);

            Row primRow = new Row();
            primRow.algorithm = "Prim";
            primRow.graph = spec.getGraphName();
            primRow.n = spec.n;
            primRow.m = spec.m;
            primRow.timeMs = primMedian;
            primRow.dsuOps = primMetrics.getDsuOps();
            primRow.edgeScans = primMetrics.getEdgeScans();
            primRow.comparisons = primMetrics.getComparisons();
            primRow.maxDepth = primMetrics.getMaxDepth();
            primRow.result = primResult.getTotalWeight();
            rows.add(primRow);

            System.out.println("  [Generating Directed Graph...]");
            Graph directed = GraphGenerator.generateDirected(spec.n, spec.m, 42);

            System.out.print("  -> Running Kosaraju... ");
            double[] kosarajuTimes = new double[NUM_RUNS];
            OperationMetrics kosarajuMetrics = null;
            SCCResult kosarajuResult = null;
            for (int r = 0; r < NUM_RUNS; r++) {
                kosarajuMetrics = new OperationMetrics();
                long start = System.nanoTime();
                kosarajuResult = KosarajuSCC.findSCC(directed, kosarajuMetrics);
                long elapsed = System.nanoTime() - start;
                kosarajuTimes[r] = elapsed / 1_000_000.0;
            }
            Arrays.sort(kosarajuTimes);
            double kosarajuMedian = kosarajuTimes[NUM_RUNS / 2];
            assertReference("Kosaraju SCC Count", kosarajuResult.getComponentCount(), spec.expectedSccCount);
            if (kosarajuMetrics.getEdgeScans() != 2L * spec.m) {
                throw new IllegalStateException(String.format("Kosaraju edge scans must be 2*m (%d), but got %d",
                        2L * spec.m, kosarajuMetrics.getEdgeScans()));
            }
            System.out.printf("OK (SCC Count: %d, Median Time: %.2f ms, Scans: %d == 2m)\n",
                    kosarajuResult.getComponentCount(), kosarajuMedian, kosarajuMetrics.getEdgeScans());

            Row kosarajuRow = new Row();
            kosarajuRow.algorithm = "Kosaraju";
            kosarajuRow.graph = spec.getGraphName();
            kosarajuRow.n = spec.n;
            kosarajuRow.m = spec.m;
            kosarajuRow.timeMs = kosarajuMedian;
            kosarajuRow.dsuOps = kosarajuMetrics.getDsuOps();
            kosarajuRow.edgeScans = kosarajuMetrics.getEdgeScans();
            kosarajuRow.comparisons = kosarajuMetrics.getComparisons();
            kosarajuRow.maxDepth = kosarajuMetrics.getMaxDepth();
            kosarajuRow.result = kosarajuResult.getComponentCount();
            rows.add(kosarajuRow);

            SCCResult tarjanResult = TarjanSCC.findSCC(directed);
            if (tarjanResult.getComponentCount() != kosarajuResult.getComponentCount()) {
                throw new IllegalStateException("Tarjan and Kosaraju component counts disagree: "
                        + tarjanResult.getComponentCount() + " vs " + kosarajuResult.getComponentCount());
            }

            System.out.println("  [Generating DAG...]");
            Graph dag = GraphGenerator.generateDAG(spec.n, spec.m, 42);

            System.out.print("  -> Running Kahn... ");
            double[] kahnTimes = new double[NUM_RUNS];
            OperationMetrics kahnMetrics = null;
            TopologicalResult kahnResult = null;
            for (int r = 0; r < NUM_RUNS; r++) {
                kahnMetrics = new OperationMetrics();
                long start = System.nanoTime();
                kahnResult = KahnTopologicalSort.sort(dag, kahnMetrics);
                long elapsed = System.nanoTime() - start;
                kahnTimes[r] = elapsed / 1_000_000.0;
            }
            Arrays.sort(kahnTimes);
            double kahnMedian = kahnTimes[NUM_RUNS / 2];
            if (kahnResult.hasCycle() || kahnResult.getProcessedCount() != spec.n) {
                throw new IllegalStateException("Kahn failed to order all vertices in DAG!");
            }
            if (!KahnTopologicalSort.validateOrder(dag, kahnResult.getOrder())) {
                throw new IllegalStateException("Kahn produced an INVALID topological ordering!");
            }
            if (kahnMetrics.getEdgeScans() != spec.m) {
                throw new IllegalStateException(String.format("Kahn edge scans must be m (%d), but got %d",
                        spec.m, kahnMetrics.getEdgeScans()));
            }
            System.out.printf("OK (Ordered: %d, Median Time: %.2f ms, Scans: %d == m, Valid: TRUE)\n",
                    kahnResult.getProcessedCount(), kahnMedian, kahnMetrics.getEdgeScans());

            Row kahnRow = new Row();
            kahnRow.algorithm = "Kahn";
            kahnRow.graph = spec.getGraphName();
            kahnRow.n = spec.n;
            kahnRow.m = spec.m;
            kahnRow.timeMs = kahnMedian;
            kahnRow.dsuOps = kahnMetrics.getDsuOps();
            kahnRow.edgeScans = kahnMetrics.getEdgeScans();
            kahnRow.comparisons = kahnMetrics.getComparisons();
            kahnRow.maxDepth = kahnMetrics.getMaxDepth();
            kahnRow.result = kahnResult.getProcessedCount();
            rows.add(kahnRow);

            System.out.print("  -> Running DagShortestPath... ");
            double[] dspTimes = new double[NUM_RUNS];
            OperationMetrics dspMetrics = null;
            ShortestPathResult dspResult = null;
            for (int r = 0; r < NUM_RUNS; r++) {
                dspMetrics = new OperationMetrics();
                long start = System.nanoTime();
                dspResult = DagShortestPath.findShortestPaths(dag, kahnResult.getOrder(), dspMetrics);
                long elapsed = System.nanoTime() - start;
                dspTimes[r] = elapsed / 1_000_000.0;
            }
            Arrays.sort(dspTimes);
            double dspMedian = dspTimes[NUM_RUNS / 2];
            assertReference("DAG Shortest Path Distance Sum", dspResult.getSumOfDistances(), spec.expectedDagDistanceSum);
            if (dspMetrics.getEdgeScans() != spec.m) {
                throw new IllegalStateException(String.format("DAG shortest path edge scans must be m (%d), but got %d",
                        spec.m, dspMetrics.getEdgeScans()));
            }
            System.out.printf("OK (Sum of Distances: %d, Median Time: %.2f ms, Scans: %d == m)\n",
                    dspResult.getSumOfDistances(), dspMedian, dspMetrics.getEdgeScans());

            Row dspRow = new Row();
            dspRow.algorithm = "DagShortestPath";
            dspRow.graph = spec.getGraphName();
            dspRow.n = spec.n;
            dspRow.m = spec.m;
            dspRow.timeMs = dspMedian;
            dspRow.dsuOps = dspMetrics.getDsuOps();
            dspRow.edgeScans = dspMetrics.getEdgeScans();
            dspRow.comparisons = dspMetrics.getComparisons();
            dspRow.maxDepth = dspMetrics.getMaxDepth();
            dspRow.result = dspResult.getSumOfDistances();
            rows.add(dspRow);
        }

        writeCsv(rows, Paths.get("results", "results.csv"));
        System.out.println("\n[SUCCESS] All 50 benchmark rows generated and verified against reference values!");
        System.out.println("Results written to results/results.csv");
    }

    private static void assertReference(String label, long actual, long expected) {
        if (actual != expected) {
            throw new IllegalStateException(String.format("MISMATCH in %s: actual %d != expected %d",
                    label, actual, expected));
        }
    }

    private static void writeCsv(List<Row> rows, Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result\n");
                for (Row r : rows) {
                    writer.write(r.toCsv());
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV file: " + path, e);
        }
    }
}
