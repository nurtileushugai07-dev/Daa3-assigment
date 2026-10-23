package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import java.util.ArrayDeque;

public final class Kahn {
    private Kahn() {}

    public static TopoResult run(Graph g, Metrics m) {
        if (!g.isDirected()) throw new IllegalArgumentException("topological order needs a directed graph");
        int n = g.n();
        int[] indeg = new int[n];
        for (Edge e : g.edges()) indeg[e.v]++;

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) queue.add(v);

        int[] order = new int[n];
        int count = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order[count++] = u;
            for (Edge e : g.adj(u)) {
                m.edgeScans++;
                if (--indeg[e.v] == 0) queue.add(e.v);
            }
        }
        return new TopoResult(order, count, count < n);
    }

    public static boolean isValidOrder(Graph g, int[] order) {
        int n = g.n();
        if (order.length != n) return false;
        int[] pos = new int[n];
        java.util.Arrays.fill(pos, -1);
        for (int i = 0; i < n; i++) {
            if (order[i] < 0 || order[i] >= n || pos[order[i]] != -1) return false;
            pos[order[i]] = i;
        }
        for (Edge e : g.edges()) if (pos[e.u] >= pos[e.v]) return false;
        return true;
    }
}
