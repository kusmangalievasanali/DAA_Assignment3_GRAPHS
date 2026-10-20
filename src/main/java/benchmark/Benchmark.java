package benchmark;

//dwdwdwd
import algorithms.DAGShortestPaths;
import algorithms.MST;
import algorithms.SCC;
import graph.Edge;
import graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Benchmark {

    private static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    private static final int[] MULTIPLIERS = {4, 20};
    private static final int RUNS = 5;

    private static final long[][] REFERENCE = {
            {147970L, 27L, 396274L},
            {30363L, 1L, 252411L},
            {468911L, 137L, 1271911L},
            {97589L, 1L, 723157L},
            {1486139L, 373L, 3941828L},
            {309550L, 1L, 2381342L},
            {4715515L, 1188L, 12551891L},
            {968517L, 1L, 7376612L},
            {15012094L, 3923L, 40096539L},
            {3056681L, 1L, 23512087L}
    };

    private static final String HEADER =
            "algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result\n";

    public static void main(String[] args) throws IOException {
        StringBuilder csv = new StringBuilder(HEADER);
        List<String> errors = new ArrayList<>();
        double[] t = new double[RUNS];
        int idx = 0;

        System.out.printf("%-8s %-9s | %-14s %-14s | %-6s | %-14s%n",
                "n", "m", "MST Kruskal", "MST Prim", "SCC", "DAG sum");

        for (int n : SIZES) {
            for (int mult : MULTIPLIERS) {
                int m = n * mult;
                String kind = (mult == 4) ? "sparse" : "dense";
                long[] ref = REFERENCE[idx++];
                String tag = n + "/" + m;

                Graph undirected = generateUndirected(n, m);
                Graph directed = generateDirected(n, m);
                Graph dag = generateDag(n, m);

                MST.Result kr = null;
                for (int i = -1; i < RUNS; i++) {
                    long s = System.nanoTime();
                    kr = MST.kruskal(undirected);
                    long e = System.nanoTime();
                    if (i >= 0) {
                        t[i] = (e - s) / 1_000_000.0;
                    }
                }
                double kTime = medianOf(t);

                MST.Result pr = null;
                for (int i = -1; i < RUNS; i++) {
                    long s = System.nanoTime();
                    pr = MST.prim(undirected);
                    long e = System.nanoTime();
                    if (i >= 0) {
                        t[i] = (e - s) / 1_000_000.0;
                    }
                }
                double pTime = medianOf(t);

                SCC.KosarajuResult sr = null;
                for (int i = -1; i < RUNS; i++) {
                    long s = System.nanoTime();
                    sr = SCC.kosaraju(directed);
                    long e = System.nanoTime();
                    if (i >= 0) {
                        t[i] = (e - s) / 1_000_000.0;
                    }
                }
                double sTime = medianOf(t);

                SCC.KahnResult hr = null;
                for (int i = -1; i < RUNS; i++) {
                    long s = System.nanoTime();
                    hr = SCC.kahn(dag);
                    long e = System.nanoTime();
                    if (i >= 0) {
                        t[i] = (e - s) / 1_000_000.0;
                    }
                }
                double hTime = medianOf(t);

                DAGShortestPaths.Result dr = null;
                for (int i = -1; i < RUNS; i++) {
                    long s = System.nanoTime();
                    dr = DAGShortestPaths.compute(dag);
                    long e = System.nanoTime();
                    if (i >= 0) {
                        t[i] = (e - s) / 1_000_000.0;
                    }
                }
                double dTime = medianOf(t);

                long sumDist = 0;
                for (long d : dr.getDistances()) {
                    if (d < Long.MAX_VALUE / 4) {
                        sumDist += d;
                    }
                }

                expect(errors, tag + " Kruskal weight", kr.getTotalWeight(), ref[0]);
                expect(errors, tag + " Prim weight", pr.getTotalWeight(), ref[0]);
                expect(errors, tag + " Kruskal vs Prim", kr.getTotalWeight(), pr.getTotalWeight());
                expect(errors, tag + " Kosaraju components", sr.getSccCount(), ref[1]);
                expect(errors, tag + " Kosaraju edge scans (2m)", sr.getEdgeScans(), 2L * m);
                expect(errors, tag + " Kahn order size", hr.getOrder().size(), n);
                expect(errors, tag + " Kahn edge scans (m)", hr.getEdgeScans(), m);
                expect(errors, tag + " DAG distance sum", sumDist, ref[2]);
                expect(errors, tag + " DAG edge scans (m)", dr.getEdgeScans(), m);
                if (kr.getEdgeScans() > m) {
                    errors.add(tag + " Kruskal edge scans " + kr.getEdgeScans() + " exceed m=" + m);
                }
                if (sr.getMaxDepth() <= 0) {
                    errors.add(tag + " Kosaraju max depth is not recorded");
                }
                if (!validOrder(dag, hr.getOrder())) {
                    errors.add(tag + " Kahn order violates pos[u] < pos[v]");
                }

                System.out.printf(Locale.US, "%-8d %-9d | %-14d %-14d | %-6d | %-14d%n",
                        n, m, kr.getTotalWeight(), pr.getTotalWeight(), sr.getSccCount(), sumDist);

                csv.append(String.format(Locale.US, "Kruskal,%s,%d,%d,%.2f,%d,%d,%d,0,%d\n",
                        kind, n, m, kTime, kr.getDsuOps(), kr.getEdgeScans(), kr.getComparisons(),
                        kr.getTotalWeight()));
                csv.append(String.format(Locale.US, "Prim,%s,%d,%d,%.2f,%d,%d,%d,0,%d\n",
                        kind, n, m, pTime, pr.getDsuOps(), pr.getEdgeScans(), pr.getComparisons(),
                        pr.getTotalWeight()));
                csv.append(String.format(Locale.US, "Kosaraju,%s,%d,%d,%.2f,0,%d,0,%d,%d\n",
                        kind, n, m, sTime, sr.getEdgeScans(), sr.getMaxDepth(), sr.getSccCount()));
                csv.append(String.format(Locale.US, "Kahn,%s,%d,%d,%.2f,0,%d,0,0,%d\n",
                        kind, n, m, hTime, hr.getEdgeScans(), hr.getOrder().size()));
                csv.append(String.format(Locale.US, "DagShortestPath,%s,%d,%d,%.2f,0,%d,0,0,%d\n",
                        kind, n, m, dTime, dr.getEdgeScans(), sumDist));
            }
        }

        File dir = new File("results");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!errors.isEmpty()) {
            try (FileWriter w = new FileWriter("results/results_FAILED.csv")) {
                w.write(csv.toString());
            }
            System.err.println();
            System.err.println("BENCHMARK FAILED: " + errors.size() + " check(s) did not pass");
            for (String e : errors) {
                System.err.println("  - " + e);
            }
            System.err.println("results/results.csv was NOT written (debug copy: results/results_FAILED.csv)");
            System.exit(1);
        }

        try (FileWriter w = new FileWriter("results/results.csv")) {
            w.write(csv.toString());
        }
        System.out.println();
        System.out.println("All checks passed. 50 rows saved to results/results.csv");
    }

    private static double medianOf(double[] times) {
        double[] copy = Arrays.copyOf(times, times.length);
        Arrays.sort(copy);
        return copy[copy.length / 2];
    }

    private static void expect(List<String> errors, String what, long got, long want) {
        if (got != want) {
            errors.add(what + ": got " + got + ", expected " + want);
        }
    }

    private static boolean validOrder(Graph g, Iterable<?> order) {
        int[] pos = new int[g.n()];
        Arrays.fill(pos, -1);
        int p = 0;
        for (Object o : order) {
            pos[((Number) o).intValue()] = p++;
        }
        for (Edge e : g.edges()) {
            if (pos[e.u] < 0 || pos[e.v] < 0 || pos[e.u] >= pos[e.v]) {
                return false;
            }
        }
        return true;
    }

    private static int[] shuffled(int n, Random rnd) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return a;
    }

    private static Graph generateUndirected(int n, int targetM) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, false);
        int[] perm = shuffled(n, rnd);
        int added = 0;
        for (int i = 1; i < n; i++) {
            int a = perm[rnd.nextInt(i)];
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(a, perm[i], w);
            added++;
        }
        while (added < targetM) {
            int a = rnd.nextInt(n);
            int b;
            do {
                b = rnd.nextInt(n);
            } while (b == a);
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(a, b, w);
            added++;
        }
        return g;
    }

    private static Graph generateDirected(int n, int targetM) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int added = 0;
        while (added < targetM) {
            int a = rnd.nextInt(n);
            int b;
            do {
                b = rnd.nextInt(n);
            } while (b == a);
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(a, b, w);
            added++;
        }
        return g;
    }

    private static Graph generateDag(int n, int targetM) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int[] rank = shuffled(n, rnd);
        int added = 0;
        while (added < targetM) {
            int a = rnd.nextInt(n);
            int b;
            do {
                b = rnd.nextInt(n);
            } while (b == a);
            if (rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(a, b, w);
            added++;
        }
        return g;
    }
}