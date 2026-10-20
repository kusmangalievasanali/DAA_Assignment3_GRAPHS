package algorithms;

import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathsTest {

    @Test
    void testDAGShortestPathsSimple() {
        Graph g = new Graph(4, false);
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
    }

    @Test
    void testNegativeWeightsAllowed() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, -2);

        DAGShortestPaths.Result res = DAGShortestPaths.compute(g);
        assertEquals(3, res.getDistances()[2]);

        List<Integer> path = DAGShortestPaths.restorePath(res.getParents(), 2);
        assertEquals(List.of(0, 1, 2), path);
    }
}