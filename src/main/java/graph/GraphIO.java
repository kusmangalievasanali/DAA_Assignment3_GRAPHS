package graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GraphIO {
    private GraphIO() {
    }

    public static Graph read(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line = nextLine(br);
            if (line == null) {
                throw new IllegalArgumentException("empty file: " + path);
            }
            String[] head = line.split("\\s+");
            if (head.length != 3) {
                throw new IllegalArgumentException("header must be 'n m directed|undirected': " + line);
            }
            int n = Integer.parseInt(head[0]);
            int m = Integer.parseInt(head[1]);
            boolean directed;
            if (head[2].equals("directed")) {
                directed = true;
            } else if (head[2].equals("undirected")) {
                directed = false;
            } else {
                throw new IllegalArgumentException("unknown graph kind: " + head[2]);
            }

            Graph g = new Graph(n, directed);
            for (int i = 0; i < m; i++) {
                line = nextLine(br);
                if (line == null) {
                    throw new IllegalArgumentException("expected " + m + " edges, found only " + i);
                }
                String[] t = line.split("\\s+");
                if (t.length != 3) {
                    throw new IllegalArgumentException("edge must be 'u v w': " + line);
                }
                g.addEdge(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            }
            return g;
        }
    }

    private static String nextLine(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                return line;
            }
        }
        return null;
    }
}