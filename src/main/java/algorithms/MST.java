package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class MST {

    public static class Result {
        private final long totalWeight;
        private final List<Edge> edges;
        private final int components;
        private final long edgeScans;
        private final long comparisons;
        private final long dsuOps;

        public Result(long totalWeight, List<Edge> edges, int components, long edgeScans, long comparisons, long dsuOps) {
            this.totalWeight = totalWeight;
            this.edges = edges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
            this.dsuOps = dsuOps;
        }

        public long getTotalWeight() { return totalWeight; }
        public List<Edge> getEdges() { return edges; }
        public int getComponents() { return components; }
        public long getEdgeScans() { return edgeScans; }
        public long getComparisons() { return comparisons; }
        public long getDsuOps() { return dsuOps; }
    }


    public static Result kruskal(Graph graph) {
        int n = graph.getV();
        List<Edge> allEdges = new ArrayList<>();
        // Для неориентированного графа соберем все уникальные рёбра
        boolean[][] visited = new boolean[n][n];
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdj(u)) {
                int v = e.to;
                if (!visited[u][v] && !visited[v][u]) {
                    allEdges.add(e);
                    visited[u][v] = true;
                    visited[v][u] = true;
                }
            }
        }

        long edgeScans = allEdges.size();
        long comparisons = 0;

        allEdges.sort((e1, e2) -> {
            // Лямбда сортировки совершает сравнения
            return Integer.compare(e1.weight, e2.weight);
        });


        class CountingComparator implements java.util.Comparator<Edge> {
            long compCount = 0;
            @Override
            public int compare(Edge a, Edge b) {
                compCount++;
                return Integer.compare(a.weight, b.weight);
            }
        }
        CountingComparator comp = new CountingComparator();
        allEdges.sort(comp);
        comparisons = comp.compCount;

        DSU dsu = new DSU(n);
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;

        for (Edge e : allEdges) {
            edgeScans++;
            int rootU = dsu.find(e.from);
            int rootV = dsu.find(e.to);
            if (rootU != rootV) {
                dsu.union(e.from, e.to);
                mstEdges.add(e);
                totalWeight += e.weight;
            }
        }

        return new Result(totalWeight, mstEdges, dsu.components(), edgeScans, comparisons, dsu.ops());
    }

    public static Result prim(Graph graph) {
        int n = graph.getV();
        if (n == 0) {
            return new Result(0, new ArrayList<>(), 0, 0, 0, 0);
        }

        boolean[] inMST = new boolean[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;
        long comparisons = 0;
        int components = 0;

        for (int startVertex = 0; startVertex < n; startVertex++) {
            if (!inMST[startVertex]) {
                components++;
                pq.add(new Edge(-1, startVertex, 0)); // Виртуальное ребро в стартовую вершину

                while (!pq.isEmpty()) {
                    Edge e = pq.poll();
                    comparisons++;

                    int u = e.to;
                    if (inMST[u]) {
                        continue;
                    }
                    inMST[u] = true;

                    if (e.from != -1) {
                        mstEdges.add(e);
                        totalWeight += e.weight;
                    }

                    for (Edge neighborEdge : graph.getAdj(u)) {
                        edgeScans++; // Сканируем исходящее ребро
                        if (!inMST[neighborEdge.to]) {
                            pq.add(neighborEdge);
                        }
                    }
                }
            }
        }

        return new Result(totalWeight, mstEdges, components, edgeScans, comparisons, 0);
    }
}