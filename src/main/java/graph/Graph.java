package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {
    private final int n;
    private final boolean directed;
    private final List<Edge>[] adj;
    private final List<Edge> edges = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0: " + n);
        }
        this.n = n;
        this.directed = directed;
        this.adj = new List[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    public void addEdge(int u, int v, int w) {
        checkVertex(u);
        checkVertex(v);
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj[u].add(e);
        if (!directed && u != v) {
            adj[v].add(e);
        }
    }

    public int n() {
        return n;
    }

    public int m() {
        return edges.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Edge> neighbors(int v) {
        checkVertex(v);
        return Collections.unmodifiableList(adj[v]);
    }

    public List<Edge> adj(int v) {
        return adj[v];
    }

    public int[] inDegrees() {
        if (!directed) {
            throw new IllegalStateException("inDegrees() needs a directed graph");
        }
        int[] deg = new int[n];
        for (Edge e : edges) {
            deg[e.v]++;
        }
        return deg;
    }

    public Graph reverse() {
        if (!directed) {
            throw new IllegalStateException("reverse() needs a directed graph");
        }
        Graph r = new Graph(n, true);
        for (Edge e : edges) {
            r.addEdge(e.v, e.u, e.w);
        }
        return r;
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= n) {
            throw new IllegalArgumentException("vertex " + v + " is out of range 0.." + (n - 1));
        }
    }
}