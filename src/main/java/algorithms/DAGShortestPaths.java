package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DAGShortestPaths {

    private static final long INF = Long.MAX_VALUE / 2;

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
        int n = graph.n();
        SCC.KahnResult kahnRes = SCC.kahn(graph);
        if (kahnRes.isHasCycle()) {
            throw new IllegalArgumentException("graph has a cycle, shortest paths in DAG are undefined");
        }
        List<Integer> topoOrder = kahnRes.getOrder();

        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] inDegree = graph.inDegrees();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        long edgeScans = 0;
        for (int u : topoOrder) {
            if (dist[u] == INF) {
                continue;
            }
            for (Edge e : graph.adj(u)) {
                edgeScans++;
                int v = e.v;
                if (dist[u] + e.w < dist[v]) {
                    dist[v] = dist[u] + e.w;
                    parent[v] = u;
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