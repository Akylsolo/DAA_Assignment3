# DAA Assignment 3: Graphs (DSU, MST, SCC, and Shortest Paths)

**Author:** Muratbek akyl  
**Group:** SE 2523  
**Repository Branch:** `main`  
**Release Tag:** `v1.0`  

---

## Overview

This project provides a complete, high-performance graph processing framework implementing core algorithms for network design, dependency analysis, cycle detection, and pipeline routing:
- **Graph Core:** Adjacency list supporting both directed and undirected graphs, self-loops, parallel edges, and isolated vertices.
- **Disjoint Set Union (DSU):** Optimized with union by rank and path compression, providing almost-constant $\alpha(n)$ amortized operations with internal operation counters.
- **Minimum Spanning Tree (MST):** Kruskal's algorithm (using DSU with early termination) and Prim's algorithm (using a min-priority queue), with automatic weight agreement validation and disconnected forest support.
- **Strongly Connected Components (SCC):** Iterative Kosaraju's two-pass algorithm and iterative Tarjan's algorithm (Bonus Task A), both surviving deep call chains on $n \ge 100\,000$ vertices without stack overflow.
- **Topological Sorting & Cycle Detection:** Kahn's in-degree queue algorithm with topological ordering validation.
- **DAG Shortest Paths:** Single-pass $\mathcal{O}(n + m)$ topological relaxation from a virtual super-source, safely supporting arbitrary edge weights and full path restoration.
- **Condensation Graph & Critical Path (Bonus Task B):** Contracting SCCs into an acyclic DAG and computing the longest critical path for project scheduling.

All algorithms include reproducible internal operation counters (`dsuOps`, `edgeScans`, `comparisons`, `maxDepth`).

---

## Requirements

- **Java JDK:** Version 21 or higher (tested with OpenJDK 26).
- **Maven:** Version 3.9+ (or use the included `mvnw` / `mvnw.cmd` wrapper).
- **Python (Optional for Plots):** Python 3.10+ with `matplotlib` and `numpy`.

---

## Quick Start: Build, Test, and Benchmark

### 1. Build the Project
Using the Maven wrapper on Windows PowerShell / CMD:
```bash
.\mvnw.cmd clean compile
```
Or on Linux / macOS:
```bash
./mvnw clean compile
```

### 2. Run All JUnit 5 Tests
Executes 15 comprehensive automated test suites including randomized cross-checks against brute-force BFS, Bellman-Ford, and 100 000-vertex stack safety tests:
```bash
.\mvnw.cmd test
```

### 3. Run the Self-Checking Benchmark Suite
Executes all 5 algorithms across the 10 reference graphs (50 evaluations total, 5 runs per algorithm measuring median wall-clock time), verifies reference values, and outputs `results/results.csv`:
```bash
java -Xmx4g -cp target/classes benchmark.BenchmarkRunner
```
Or via Maven:
```bash
.\mvnw.cmd compile exec:java
```

### 4. Regenerate Plots
To generate the three evaluation charts in `results/plots/`:
```bash
py scripts/generate_plots.py
```

---

## Project Structure

```
Daa_Assignment3/
├── .gitignore
├── mvnw / mvnw.cmd
├── pom.xml
├── README.md
├── REPORT.md
├── data/
│   ├── graph_disconnected.txt
│   ├── graph_cyclic.txt
│   └── graph_dag.txt
├── results/
│   ├── results.csv
│   └── plots/
│       ├── plot1_operations_vs_n.png
│       ├── plot2_scans_per_edge.png
│       └── plot3_kruskal_edge_share.png
├── scripts/
│   └── generate_plots.py
└── src/
    ├── main/java/
    │   ├── graph/
    │   │   ├── Edge.java
    │   │   ├── Graph.java
    │   │   ├── GraphReader.java
    │   │   └── GraphGenerator.java
    │   ├── algorithms/
    │   │   ├── dsu/DisjointSetUnion.java
    │   │   ├── mst/KruskalMST.java, PrimMST.java, MSTResult.java
    │   │   ├── scc/KosarajuSCC.java, TarjanSCC.java, SCCResult.java
    │   │   ├── topo/KahnTopologicalSort.java, TopologicalResult.java
    │   │   └── paths/DagShortestPath.java, ShortestPathResult.java, CriticalPathDAG.java
    │   ├── metrics/OperationMetrics.java
    │   └── benchmark/BenchmarkRunner.java
    └── test/java/
        ├── algorithms/
        │   ├── DSUTest.java
        │   ├── MSTTest.java
        │   ├── SCCTest.java
        │   ├── TopologicalOrderTest.java
        │   ├── DAGShortestPathTest.java
        │   └── BonusTasksTest.java
```

---

## Benchmark Reference Values Verification

All generated numbers match the official reference table 100%:

| Graph ($n$ / $m$) | MST Total Weight (Kruskal & Prim) | SCC Count (Kosaraju & Tarjan) | Sum of DAG Distances (DSP) | Reference Check |
| :--- | :---: | :---: | :---: | :---: |
| **1 000 / 4 000** | 147 970 | 27 | 396 274 | **MATCH** |
| **1 000 / 20 000** | 30 363 | 1 | 252 411 | **MATCH** |
| **3 162 / 12 648** | 468 911 | 137 | 1 271 911 | **MATCH** |
| **3 162 / 63 240** | 97 589 | 1 | 723 157 | **MATCH** |
| **10 000 / 40 000** | 1 486 139 | 373 | 3 941 828 | **MATCH** |
| **10 000 / 200 000** | 309 550 | 1 | 2 381 342 | **MATCH** |
| **31 623 / 126 492** | 4 715 515 | 1 188 | 12 551 891 | **MATCH** |
| **31 623 / 632 460** | 968 517 | 1 | 7 376 612 | **MATCH** |
| **100 000 / 400 000** | 15 012 094 | 3 923 | 40 096 539 | **MATCH** |
| **100 000 / 2 000 000** | 3 056 681 | 1 | 23 512 087 | **MATCH** |

