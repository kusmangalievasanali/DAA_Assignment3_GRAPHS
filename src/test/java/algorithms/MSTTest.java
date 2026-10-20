package algorithms;

import graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MSTTest {

    @Test
    void kruskalAndPrimAgreeOnConnectedGraph() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 3);
        g.addEdge(0, 3, 4);
        g.addEdge(1, 3, 5);

        MST.Result kruskalRes = MST.kruskal(g);
        MST.Result primRes = MST.prim(g);

        assertEquals(kruskalRes.getTotalWeight(), primRes.getTotalWeight());
        assertEquals(3, kruskalRes.getEdges().size());
        assertEquals(1, kruskalRes.getComponents());
    }

    @Test
    void handlesDisconnectedGraphGracefully() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(2, 3, 10);

        MST.Result kruskalRes = MST.kruskal(g);
        MST.Result primRes = MST.prim(g);

        assertEquals(kruskalRes.getTotalWeight(), primRes.getTotalWeight());
        assertEquals(15, kruskalRes.getTotalWeight());
        assertEquals(2, kruskalRes.getComponents());
    }
}