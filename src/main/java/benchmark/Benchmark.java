package benchmark;

import algorithms.DAGShortestPaths;
import algorithms.MST;
import algorithms.SCC;
import graph.Edge;
import graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Benchmark {

    public static void main(String[] args) throws IOException {
        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }

        FileWriter writer = new FileWriter("results/results.csv");
        writer.write("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_c,result\n");

        int[] sizes = {1000, 3162, 10000, 31623, 100000};
        int[] multipliers = {4, 20};

        for (int n : sizes) {
            for (int mult : multipliers) {
                int m = n * mult;
                String graphTypeDesc = (mult == 4) ? "sparse" : "dense";

                Graph undirected = generateUndirected(n, m);
                Graph directed = generateDirected(n, m);
                Graph dag = generateDag(n, m);

                runAndWriteMST("Kruskal", undirected, n, m, graphTypeDesc, writer);
                runAndWriteMST("Prim", undirected, n, m, graphTypeDesc, writer);

                runAndWriteSCC("Kosaraju", directed, n, m, graphTypeDesc, writer);
                runAndWriteKahn("Kahn", dag, n, m, graphTypeDesc, writer);
                runAndWriteDAGSP("DagShortestPath", dag, n, m, graphTypeDesc, writer);
            }
        }

        writer.close();
        System.out.println("Benchmark completed. Results saved to results/results.csv");
    }

    private static Graph generateUndirected(int n, int targetM) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, false);
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }

        int addedEdges = 0;
        for (int i = 1; i < n; i++) {
            int parent = perm[rnd.nextInt(i)];
            int child = perm[i];
            int w = 1 + rnd.nextInt(1000);
            g.addEdge(parent, child, w);
            addedEdges++;
        }

        while (addedEdges < targetM) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a != b) {
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
                addedEdges++;
            }
        }
        return g;
    }

    private static Graph generateDirected(int n, int targetM) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int addedEdges = 0;
        while (addedEdges < targetM) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a != b) {
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
                addedEdges++;
            }
        }
        return g;
    }

    private static Graph generateDag(int n, int targetM) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = rank[i];
            rank[i] = rank[j];
            rank[j] = tmp;
        }

        int addedEdges = 0;
        while (addedEdges < targetM) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a != b) {
                if (rank[a] > rank[b]) {
                    int t = a;
                    a = b;
                    b = t;
                }
                int w = 1 + rnd.nextInt(1000);
                g.addEdge(a, b, w);
                addedEdges++;
            }
        }
        return g;
    }

    private static void runAndWriteMST(String algName, Graph g, int n, int m, String graphDesc, FileWriter w) throws IOException {
        double[] times = new double[5];
        MST.Result res = null;
        for (int i = -1; i < 5; i++) {
            long start = System.nanoTime();
            if (algName.equals("Kruskal")) {
                res = MST.kruskal(g);
            } else {
                res = MST.prim(g);
            }
            long end = System.nanoTime();
            if (i >= 0) {
                times[i] = (end - start) / 1_000_000.0;
            }
        }
        Arrays.sort(times);
        double medianTime = times[2];
        w.write(String.format(Locale.US, "%s,%s,%d,%d,%.2f,%d,%d,%d,0,%d\n",
                algName, graphDesc, n, m, medianTime, res.getDsuOps(), res.getEdgeScans(), res.getComparisons(), res.getTotalWeight()));
    }

    private static void runAndWriteSCC(String algName, Graph g, int n, int m, String graphDesc, FileWriter w) throws IOException {
        double[] times = new double[5];
        SCC.KosarajuResult res = null;
        for (int i = -1; i < 5; i++) {
            long start = System.nanoTime();
            res = SCC.kosaraju(g);
            long end = System.nanoTime();
            if (i >= 0) {
                times[i] = (end - start) / 1_000_000.0;
            }
        }
        Arrays.sort(times);
        double medianTime = times[2];
        w.write(String.format(Locale.US, "%s,%s,%d,%d,%.2f,0,%d,0,%d,%d\n",
                algName, graphDesc, n, m, medianTime, res.getEdgeScans(), res.getMaxDepth(), res.getSccCount()));
    }

    private static void runAndWriteKahn(String algName, Graph g, int n, int m, String graphDesc, FileWriter w) throws IOException {
        double[] times = new double[5];
        SCC.KahnResult res = null;
        for (int i = -1; i < 5; i++) {
            long start = System.nanoTime();
            res = SCC.kahn(g);
            long end = System.nanoTime();
            if (i >= 0) {
                times[i] = (end - start) / 1_000_000.0;
            }
        }
        Arrays.sort(times);
        double medianTime = times[2];
        w.write(String.format(Locale.US, "%s,%s,%d,%d,%.2f,0,%d,0,0,%d\n",
                algName, graphDesc, n, m, medianTime, res.getEdgeScans(), res.getOrder().size()));
    }

    private static void runAndWriteDAGSP(String algName, Graph g, int n, int m, String graphDesc, FileWriter w) throws IOException {
        double[] times = new double[5];
        DAGShortestPaths.Result res = null;
        for (int i = -1; i < 5; i++) {
            long start = System.nanoTime();
            res = DAGShortestPaths.compute(g);
            long end = System.nanoTime();
            if (i >= 0) {
                times[i] = (end - start) / 1_000_000.0;
            }
        }
        Arrays.sort(times);
        double medianTime = times[2];
        long sumDist = 0;
        for (long d : res.getDistances()) {
            if (d < Long.MAX_VALUE / 4) sumDist += d;
        }
        w.write(String.format(Locale.US, "%s,%s,%d,%d,%.2f,0,%d,0,0,%d\n",
                algName, graphDesc, n, m, medianTime, res.getEdgeScans(), sumDist));
    }
}