package algorithms;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SCCTest {

    private static boolean[][] reachability(Graph g) {
        int n = g.n();
        boolean[][] reach = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            reach[s][s] = true;
            queue.add(s);
            while (!queue.isEmpty()) {
                int v = queue.poll();
                for (Edge e : g.adj(v)) {
                    if (!reach[s][e.v]) {
                        reach[s][e.v] = true;
                        queue.add(e.v);
                    }
                }
            }
        }
        return reach;
    }

    private static int bruteForceComponents(Graph g) {
        int n = g.n();
        boolean[][] reach = reachability(g);
        boolean[] seen = new boolean[n];
        int count = 0;
        for (int u = 0; u < n; u++) {
            if (seen[u]) {
                continue;
            }
            count++;
            for (int v = 0; v < n; v++) {
                if (reach[u][v] && reach[v][u]) {
                    seen[v] = true;
                }
            }
        }
        return count;
    }

    @Test
    void kosarajuFindsOneComponentOnCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        assertEquals(1, SCC.kosaraju(g).getSccCount());
    }

    @Test
    void kosarajuScansExactlyTwoM() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 1, 1);
        g.addEdge(2, 3, 1);
        SCC.KosarajuResult r = SCC.kosaraju(g);
        assertEquals(3, r.getSccCount());
        assertEquals(8, r.getEdgeScans());
    }

    @Test
    void kosarajuMatchesBruteForceOnRandomGraphs() {
        Random rnd = new Random(11);
        for (int round = 0; round < 100; round++) {
            int n = 1 + rnd.nextInt(200);
            int m = rnd.nextInt(3 * n + 1);
            Graph g = new Graph(n, true);
            for (int i = 0; i < m; i++) {
                g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(1000));
            }
            assertEquals(bruteForceComponents(g), SCC.kosaraju(g).getSccCount());
        }
    }

    @Test
    void kahnGivesValidOrderOnDag() {
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 1);

        SCC.KahnResult res = SCC.kahn(g);
        assertFalse(res.isHasCycle());
        List<Integer> order = res.getOrder();
        assertEquals(5, order.size());
        int[] pos = new int[5];
        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }
        for (Edge e : g.edges()) {
            assertTrue(pos[e.u] < pos[e.v]);
        }
        assertEquals(g.m(), res.getEdgeScans());
    }

    @Test
    void kahnDetectsCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        SCC.KahnResult res = SCC.kahn(g);
        assertTrue(res.isHasCycle());
        assertTrue(res.getOrder().size() < 3);
    }

    @Test
    void stackSafetyOnLargePath() {
        int n = 100_000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }
        SCC.KosarajuResult res = assertDoesNotThrow(() -> SCC.kosaraju(g));
        assertEquals(n, res.getSccCount());
        assertTrue(res.getMaxDepth() > 0 && res.getMaxDepth() <= n);
    }
}