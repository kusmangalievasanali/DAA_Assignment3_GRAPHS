package algorithms;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathsTest {

    private static long[] bellmanFord(Graph g) {
        int n = g.n();
        long inf = Long.MAX_VALUE / 2;
        long[] dist = new long[n];
        Arrays.fill(dist, inf);
        int[] in = g.inDegrees();
        for (int i = 0; i < n; i++) {
            if (in[i] == 0) {
                dist[i] = 0;
            }
        }
        for (int round = 0; round < n; round++) {
            boolean changed = false;
            for (Edge e : g.edges()) {
                if (dist[e.u] != inf && dist[e.u] + e.w < dist[e.v]) {
                    dist[e.v] = dist[e.u] + e.w;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        return dist;
    }

    @Test
    void simpleDag() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 5);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 3);

        DAGShortestPaths.Result res = DAGShortestPaths.compute(g);
        long[] dist = res.getDistances();
        assertEquals(0, dist[0]);
        assertEquals(2, dist[1]);
        assertEquals(3, dist[2]);
        assertEquals(6, dist[3]);
        assertEquals(g.m(), res.getEdgeScans());
    }

    @Test
    void negativeWeightsAndPathRestoring() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, -2);

        DAGShortestPaths.Result res = DAGShortestPaths.compute(g);
        assertEquals(3, res.getDistances()[2]);
        List<Integer> path = DAGShortestPaths.restorePath(res.getParents(), 2);
        assertEquals(List.of(0, 1, 2), path);
    }

    @Test
    void everySourceStartsAtZero() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 2, 7);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 1);
        long[] dist = DAGShortestPaths.compute(g).getDistances();
        assertEquals(0, dist[0]);
        assertEquals(0, dist[1]);
        assertEquals(3, dist[2]);
        assertEquals(4, dist[3]);
    }

    @Test
    void cycleIsRejected() {
        Graph g = new Graph(2, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        assertThrows(IllegalArgumentException.class, () -> DAGShortestPaths.compute(g));
    }

    @Test
    void matchesBellmanFordOnRandomDagsWithNegativeWeights() {
        Random rnd = new Random(3);
        for (int round = 0; round < 100; round++) {
            int n = 2 + rnd.nextInt(40);
            int[] rank = new int[n];
            for (int i = 0; i < n; i++) {
                rank[i] = i;
            }
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int t = rank[i];
                rank[i] = rank[j];
                rank[j] = t;
            }
            Graph g = new Graph(n, true);
            int m = rnd.nextInt(4 * n);
            for (int i = 0; i < m; i++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                if (a == b) {
                    continue;
                }
                if (rank[a] > rank[b]) {
                    int t = a;
                    a = b;
                    b = t;
                }
                g.addEdge(a, b, rnd.nextInt(31) - 10);
            }
            assertArrayEquals(bellmanFord(g), DAGShortestPaths.compute(g).getDistances());
        }
    }
}