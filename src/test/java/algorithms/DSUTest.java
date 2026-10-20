package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DSUTest {

    private static class Reference {
        final int[] label;

        Reference(int n) {
            label = new int[n];
            for (int i = 0; i < n; i++) {
                label[i] = i;
            }
        }

        void union(int a, int b) {
            int la = label[a];
            int lb = label[b];
            if (la == lb) {
                return;
            }
            for (int i = 0; i < label.length; i++) {
                if (label[i] == lb) {
                    label[i] = la;
                }
            }
        }

        boolean same(int a, int b) {
            return label[a] == label[b];
        }

        int sets() {
            return (int) java.util.Arrays.stream(label).distinct().count();
        }
    }

    @Test
    void startsWithSingletons() {
        DSU d = new DSU(5);
        assertEquals(5, d.components());
        for (int i = 0; i < 5; i++) {
            assertEquals(i, d.find(i));
        }
    }

    @Test
    void unionMergesAndReportsResult() {
        DSU d = new DSU(4);
        assertTrue(d.union(0, 1));
        assertFalse(d.union(1, 0));
        assertTrue(d.union(2, 3));
        assertEquals(2, d.components());
        assertEquals(d.find(0), d.find(1));
        assertNotEquals(d.find(0), d.find(2));
    }

    @Test
    void agreesWithReferenceOnRandomUnions() {
        Random rnd = new Random(1);
        for (int round = 0; round < 50; round++) {
            int n = 1 + rnd.nextInt(100);
            DSU d = new DSU(n);
            Reference ref = new Reference(n);
            int ops = rnd.nextInt(200);
            for (int i = 0; i < ops; i++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                d.union(a, b);
                ref.union(a, b);
            }
            assertEquals(ref.sets(), d.components());
            for (int i = 0; i < 100; i++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                assertEquals(ref.same(a, b), d.find(a) == d.find(b));
            }
        }
    }

    @Test
    void countersCountFindsAndSuccessfulUnions() {
        DSU d = new DSU(4);
        d.union(0, 1);
        d.union(0, 1);
        d.union(2, 3);
        assertEquals(2, d.unionCalls());
        assertEquals(6, d.findCalls());
        assertEquals(8, d.ops());
    }

    @Test
    void makeSetResetsEverything() {
        DSU d = new DSU(3);
        d.union(0, 1);
        d.makeSet(3);
        assertEquals(3, d.components());
        assertEquals(0, d.ops());
        assertNotEquals(d.find(0), d.find(1));
    }

    @Test
    void longChainDoesNotOverflowTheStack() {
        int n = 200_000;
        DSU d = new DSU(n);
        for (int i = 1; i < n; i++) {
            d.union(i - 1, i);
        }
        assertEquals(1, d.components());
        assertEquals(d.find(0), d.find(n - 1));
    }
}