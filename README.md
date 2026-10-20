# DAA Assignment 3: Graph Algorithms

This project contains the implementation of fundamental graph algorithms and data structures as part of the Design and Analysis of Algorithms (DAA) course assignment.

## Implemented Components

1. **Disjoint Set Union (DSU)**: Implemented with path compression and rank heuristics, including exact physical counters for operations.
2. **Minimum Spanning Tree (MST)**: 
   - Kruskal's Algorithm
   - Prim's Algorithm
3. **Strongly Connected Components & Topological Sort**:
   - Kosaraju's Algorithm for SCC
   - Kahn's Algorithm for Topological Sort / Cycle detection
4. **Shortest Paths in DAG**: Single-source shortest paths in Directed Acyclic Graphs supporting negative edge weights.
5. **Benchmark**: Automated benchmarking script that measures median execution time (out of 5 runs), edge scans, DSU operations, and comparisons across various graph sizes ($N$ up to 100,000) and densities (sparse/dense).

## Project Structure

- `src/main/java/algorithms/` — Core algorithm implementations (`DSU.java`, `MST.java`, `SCC.java`, `DAGShortestPaths.java`).
- `src/main/java/graph/` — Graph representation (`Graph.java`, `Edge.java`, `GraphIO.java`).
- `src/main/java/benchmark/` — Benchmarking script (`Benchmark.java`).
- `results/` — Directory containing the generated `results.csv` performance report.

## How to Run

### 1. Run Unit Tests
Execute the test suite using Maven:
```bash
mvn test
