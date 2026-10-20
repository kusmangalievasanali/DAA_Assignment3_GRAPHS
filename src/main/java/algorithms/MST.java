package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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

    private static final class CountingComparator implements Comparator<Edge> {
        long count = 0;

        @Override
        public int compare(Edge a, Edge b) {
            count++;
            return Integer.compare(a.w, b.w);
        }
    }

    public static Result kruskal(Graph graph) {
        int n = graph.n();
        CountingComparator comp = new CountingComparator();
        List<Edge> sortedEdges = new ArrayList<>(graph.edges());
        sortedEdges.sort(comp);

        DSU dsu = new DSU(n);
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;

        for (Edge e : sortedEdges) {
            if (dsu.components() <= 1) {
                break;
            }
            edgeScans++;
            if (dsu.find(e.u) != dsu.find(e.v)) {
                dsu.union(e.u, e.v);
                mstEdges.add(e);
                totalWeight += e.w;
            }
        }

        return new Result(totalWeight, mstEdges, dsu.components(), edgeScans, comp.count, dsu.ops());
    }

    public static Result prim(Graph graph) {
        int n = graph.n();
        boolean[] inMST = new boolean[n];
        CountingComparator comp = new CountingComparator();
        PriorityQueue<Edge> pq = new PriorityQueue<>(comp);

        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (inMST[start]) {
                continue;
            }
            components++;
            inMST[start] = true;
            edgeScans += pushEdges(graph, start, inMST, pq);

            while (!pq.isEmpty()) {
                Edge e = pq.poll();
                if (inMST[e.u] && inMST[e.v]) {
                    continue;
                }
                int next = inMST[e.u] ? e.v : e.u;
                inMST[next] = true;
                mstEdges.add(e);
                totalWeight += e.w;
                edgeScans += pushEdges(graph, next, inMST, pq);
            }
        }

        return new Result(totalWeight, mstEdges, components, edgeScans, comp.count, 0);
    }

    private static long pushEdges(Graph graph, int v, boolean[] inMST, PriorityQueue<Edge> pq) {
        long scanned = 0;
        for (Edge e : graph.adj(v)) {
            scanned++;
            if (!inMST[e.to(v)]) {
                pq.add(e);
            }
        }
        return scanned;
    }
}