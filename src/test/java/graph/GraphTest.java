package graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    private static Graph load(String name) throws IOException {
        return GraphIO.read(Path.of("data", name));
    }

    @Test
    void readsDisconnectedUndirectedGraph() throws IOException {
        Graph g = load("disconnected.txt");
        assertEquals(8, g.n());
        assertEquals(8, g.m());
        assertFalse(g.isDirected());
        assertEquals(2, g.neighbors(0).size());
        assertEquals(3, g.neighbors(1).size());
        assertEquals(3, g.neighbors(5).size());
        assertTrue(g.neighbors(6).isEmpty());
        assertTrue(g.neighbors(7).isEmpty());
    }

    @Test
    void undirectedEdgeIsVisibleFromBothEnds() throws IOException {
        Graph g = load("disconnected.txt");
        boolean from3 = g.neighbors(3).stream().anyMatch(e -> e.to(3) == 4);
        boolean from4 = g.neighbors(4).stream().anyMatch(e -> e.to(4) == 3);
        assertTrue(from3 && from4);
    }

    @Test
    void readsCyclicDirectedGraph() throws IOException {
        Graph g = load("cyclic.txt");
        assertEquals(8, g.n());
        assertEquals(9, g.m());
        assertTrue(g.isDirected());
        assertEquals(1, g.neighbors(0).size());
        assertEquals(2, g.neighbors(2).size());
        assertEquals(1, g.neighbors(6).size());
    }

    @Test
    void readsDagWithNegativeWeight() throws IOException {
        Graph g = load("dag.txt");
        assertEquals(6, g.n());
        assertEquals(7, g.m());
        assertTrue(g.edges().stream().anyMatch(e -> e.w < 0));
        int[] in = g.inDegrees();
        assertEquals(0, in[0]);
        assertEquals(2, in[5]);
    }

    @Test
    void readsConnectedGraph() throws IOException {
        Graph g = load("connected.txt");
        assertEquals(5, g.n());
        assertEquals(7, g.m());
    }

    @Test
    void reverseFlipsEveryEdge() throws IOException {
        Graph g = load("cyclic.txt");
        Graph r = g.reverse();
        assertEquals(g.n(), r.n());
        assertEquals(g.m(), r.m());
        assertTrue(r.neighbors(1).stream().anyMatch(e -> e.to(1) == 0));
        assertTrue(r.neighbors(0).stream().anyMatch(e -> e.to(0) == 2));
    }

    @Test
    void reverseOfUndirectedIsRejected() throws IOException {
        Graph g = load("connected.txt");
        assertThrows(IllegalStateException.class, g::reverse);
    }

    @Test
    void edgeWithBadVertexIsRejected() {
        Graph g = new Graph(3, false);
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 3, 1));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(-1, 0, 1));
    }

    @Test
    void emptyGraphAndNoEdgesDoNotCrash() {
        Graph g = new Graph(0, true);
        assertEquals(0, g.n());
        assertEquals(0, g.m());
        Graph h = new Graph(5, false);
        assertEquals(0, h.m());
        assertTrue(h.neighbors(4).isEmpty());
    }

    @Test
    void malformedFilesAreRejected(@TempDir Path dir) throws IOException {
        Path badKind = dir.resolve("a.txt");
        Files.writeString(badKind, "2 0 sideways\n");
        assertThrows(IllegalArgumentException.class, () -> GraphIO.read(badKind));

        Path tooFew = dir.resolve("b.txt");
        Files.writeString(tooFew, "3 2 directed\n0 1 5\n");
        assertThrows(IllegalArgumentException.class, () -> GraphIO.read(tooFew));

        Path empty = dir.resolve("c.txt");
        Files.writeString(empty, "");
        assertThrows(IllegalArgumentException.class, () -> GraphIO.read(empty));
    }
}