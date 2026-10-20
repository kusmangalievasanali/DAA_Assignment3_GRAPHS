package algorithms;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MSTTest {

    @Test
    void kruskalAndPrimAgreeOnConnectedGraph() {
        Graph g = new Graph(4, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 3);
        g.addEdge(0, 3, 4);
        g.addEdge(1, 3, 5);

        MST.Result k = MST.kruskal(g);
        MST.Result p = MST.prim(g);

        assertEquals(6, k.getTotalWeight());
        assertEquals(6, p.getTotalWeight());
        assertEquals(3, k.getEdges().size());
        assertEquals(3, p.getEdges().size());
        assertEquals(1, k.getComponents());
        assertEquals(1, p.getComponents());
    }

    @Test
    void handlesDisconnectedGraphGracefully() {
        Graph g = new Graph(4, false);
        g.addEdge(0, 1, 5);
        g.addEdge(2, 3, 10);

        MST.Result k = MST.kruskal(g);
        MST.Result p = MST.prim(g);

        assertEquals(15, k.getTotalWeight());
        assertEquals(15, p.getTotalWeight());
        assertEquals(2, k.getComponents());
        assertEquals(2, p.getComponents());
    }

    @Test
    void agreeOnHundredRandomConnectedGraphs() {
        Random rnd = new Random(7);
        for (int round = 0; round < 100; round++) {
            int n = 2 + rnd.nextInt(60);
            Graph g = new Graph(n, false);
            for (int i = 1; i < n; i++) {
                g.addEdge(rnd.nextInt(i), i, 1 + rnd.nextInt(1000));
            }
            int extra = rnd.nextInt(200);
            for (int i = 0; i < extra; i++) {
                g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(1000));
            }
            MST.Result k = MST.kruskal(g);
            MST.Result p = MST.prim(g);
            assertEquals(k.getTotalWeight(), p.getTotalWeight());
            assertEquals(n - 1, k.getEdges().size());
            assertEquals(n - 1, p.getEdges().size());
        }
    }

    @Test
    void selfLoopsParallelEdgesAndIsolatedVerticesAreHandled() {
        Graph g = new Graph(5, false);
        g.addEdge(0, 0, 1);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 4);

        MST.Result k = MST.kruskal(g);
        MST.Result p = MST.prim(g);
        assertEquals(6, k.getTotalWeight());
        assertEquals(6, p.getTotalWeight());
        assertEquals(3, k.getComponents());
        assertEquals(3, p.getComponents());
    }

    @Test
    void kruskalStopsEarlyAndNeverScansMoreThanM() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(0, 2, 3);
        g.addEdge(0, 2, 4);
        g.addEdge(1, 2, 5);
        MST.Result k = MST.kruskal(g);
        assertEquals(2, k.getEdgeScans());
    }

    @Test
    void emptyGraphDoesNotCrash() {
        assertEquals(0, MST.kruskal(new Graph(0, false)).getComponents());
        assertEquals(0, MST.prim(new Graph(0, false)).getComponents());
    }
}