package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SCC {

    public static class KosarajuResult {
        private final int sccCount;
        private final long edgeScans;
        private final long maxDepth;

        public KosarajuResult(int sccCount, long edgeScans, long maxDepth) {
            this.sccCount = sccCount;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }

        public int getSccCount() {
            return sccCount;
        }

        public long getEdgeScans() {
            return edgeScans;
        }

        public long getMaxDepth() {
            return maxDepth;
        }
    }

    public static KosarajuResult kosaraju(Graph graph) {
        int n = graph.n();
        boolean[] visited = new boolean[n];
        ArrayDeque<Integer> finishOrder = new ArrayDeque<>();
        long edgeScans = 0;
        long maxDepth = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                ArrayDeque<Integer> stack = new ArrayDeque<>();
                ArrayDeque<Integer> stateStack = new ArrayDeque<>();
                stack.push(i);
                stateStack.push(0);
                visited[i] = true;

                while (!stack.isEmpty()) {
                    if (stack.size() > maxDepth) {
                        maxDepth = stack.size();
                    }
                    int u = stack.peek();
                    int edgeIndex = stateStack.pop();
                    List<Edge> neighbors = graph.adj(u);

                    if (edgeIndex < neighbors.size()) {
                        stateStack.push(edgeIndex + 1);
                        Edge e = neighbors.get(edgeIndex);
                        edgeScans++;
                        int v = e.v;
                        if (!visited[v]) {
                            visited[v] = true;
                            stack.push(v);
                            stateStack.push(0);
                        }
                    } else {
                        stack.pop();
                        finishOrder.push(u);
                    }
                }
            }
        }

        Graph reversed = graph.reverse();
        Arrays.fill(visited, false);
        int sccCount = 0;

        for (int start : finishOrder) {
            if (!visited[start]) {
                sccCount++;
                ArrayDeque<Integer> stack = new ArrayDeque<>();
                stack.push(start);
                visited[start] = true;

                while (!stack.isEmpty()) {
                    if (stack.size() > maxDepth) {
                        maxDepth = stack.size();
                    }
                    int u = stack.pop();
                    for (Edge e : reversed.adj(u)) {
                        edgeScans++;
                        int v = e.v;
                        if (!visited[v]) {
                            visited[v] = true;
                            stack.push(v);
                        }
                    }
                }
            }
        }

        return new KosarajuResult(sccCount, edgeScans, maxDepth);
    }

    public static class KahnResult {
        private final List<Integer> order;
        private final boolean hasCycle;
        private final long edgeScans;

        public KahnResult(List<Integer> order, boolean hasCycle, long edgeScans) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
        }

        public List<Integer> getOrder() {
            return order;
        }

        public boolean isHasCycle() {
            return hasCycle;
        }

        public long getEdgeScans() {
            return edgeScans;
        }
    }

    public static KahnResult kahn(Graph graph) {
        int n = graph.n();
        int[] inDegree = graph.inDegrees();
        long edgeScans = 0;

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Edge e : graph.adj(u)) {
                edgeScans++;
                int v = e.v;
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        boolean hasCycle = order.size() < n;
        return new KahnResult(order, hasCycle, edgeScans);
    }
}