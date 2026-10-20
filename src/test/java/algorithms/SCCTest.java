package algorithms;

import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SCCTest {

    @Test
    void testKosarajuSimple() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        SCC.KosarajuResult res = SCC.kosaraju(g);
        assertEquals(1, res.getSccCount());
    }

    @Test
    void testKahnValidDAG() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);

        SCC.KahnResult res = SCC.kahn(g);
        assertFalse(res.isHasCycle());
        assertEquals(3, res.getOrder().size());
    }

    @Test
    void testKahnCycleDetection() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        SCC.KahnResult res = SCC.kahn(g);
        assertTrue(res.isHasCycle());
    }

    @Test
    void stackSafetyOnLargePath() {
        int n = 100_000;
        Graph g = new Graph(n, false);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }

        SCC.KosarajuResult res = SCC.kosaraju(g);
        assertEquals(n, res.getSccCount());
        assertTrue(res.getMaxDepth() <= n);
    }
}