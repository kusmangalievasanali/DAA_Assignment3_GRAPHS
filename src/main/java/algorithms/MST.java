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
        int n = graph.n();
        List<Edge> allEdges = graph.edges();
        long edgeScans = allEdges.size();

        class CountingComparator implements java.util.Comparator<Edge> {
            long compCount = 0;
            @Override
            public int compare(Edge a, Edge b) {
                compCount++;
                return Integer.compare(a.w, b.w);
            }
        }
        CountingComparator comp = new CountingComparator();

        List<Edge> sortedEdges = new ArrayList<>(allEdges);
        sortedEdges.sort(comp);
        long comparisons = comp.compCount;

        DSU dsu = new DSU(n);
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;

        for (Edge e : sortedEdges) {
            edgeScans++;
            int rootU = dsu.find(e.u);
            int rootV = dsu.find(e.v);
            if (rootU != rootV) {
                dsu.union(e.u, e.v);
                mstEdges.add(e);
                totalWeight += e.w;
            }
        }

        return new Result(totalWeight, mstEdges, dsu.components(), edgeScans, comparisons, dsu.ops());
    }

    public static Result prim(Graph graph) {
        int n = graph.n();
        if (n == 0) {
            return new Result(0, new ArrayList<>(), 0, 0, 0, 0);
        }

        boolean[] inMST = new boolean[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.w));

        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;
        long comparisons = 0;
        int components = 0;

        for (int startVertex = 0; startVertex < n; startVertex++) {
            if (!inMST[startVertex]) {
                components++;
                pq.add(new Edge(-1, startVertex, 0));

                while (!pq.isEmpty()) {
                    Edge e = pq.poll();
                    comparisons++;

                    int u = e.v;
                    if (inMST[u]) {
                        continue;
                    }
                    inMST[u] = true;

                    if (e.u != -1) {
                        mstEdges.add(e);
                        totalWeight += e.w;
                    }

                    for (Edge neighborEdge : graph.adj(u)) {
                        edgeScans++;
                        int neighbor = (neighborEdge.u == u) ? neighborEdge.v : neighborEdge.u;
                        if (!inMST[neighbor]) {
                            pq.add(neighborEdge);
                        }
                    }
                }
            }
        }

        return new Result(totalWeight, mstEdges, components, edgeScans, comparisons, 0);
    }
}