package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class DAGShortestPaths {

    public static class Result {
        private final long[] distances;
        private final int[] parents;
        private final long edgeScans;

        public Result(long[] distances, int[] parents, long edgeScans) {
            this.distances = distances;
            this.parents = parents;
            this.edgeScans = edgeScans;
        }

        public long[] getDistances() {
            return distances;
        }

        public int[] getParents() {
            return parents;
        }

        public long getEdgeScans() {
            return edgeScans;
        }
    }

    public static Result compute(Graph graph) {
        int n = graph.getV();
        SCC.KahnResult kahnRes = SCC.kahn(graph);
        List<Integer> topoOrder = kahnRes.getOrder();

        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE / 2);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.getAdj(u)) {
                inDegree[e.to]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        long edgeScans = kahnRes.getEdgeScans();

        for (int u : topoOrder) {
            if (dist[u] != Long.MAX_VALUE / 2) {
                for (Edge e : graph.getAdj(u)) {
                    edgeScans++;
                    int v = e.to;
                    if (dist[u] + e.weight < dist[v]) {
                        dist[v] = dist[u] + e.weight;
                        parent[v] = u;
                    }
                }
            }
        }

        return new Result(dist, parent, edgeScans);
    }

    public static List<Integer> restorePath(int[] parent, int target) {
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = parent[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}