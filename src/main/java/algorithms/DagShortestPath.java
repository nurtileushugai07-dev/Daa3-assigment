package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import java.util.Arrays;


public final class DagShortestPath {
    private DagShortestPath() {}

    public static DagResult run(Graph g, int[] order, Metrics m) {
        if (!g.isDirected()) throw new IllegalArgumentException("needs a directed graph");
        int n = g.n();
        if (order.length != n) throw new IllegalArgumentException("order must contain all vertices (graph has a cycle?)");

        int[] indeg = new int[n];
        for (Edge e : g.edges()) indeg[e.v]++;

        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, DagResult.INF);
        Arrays.fill(parent, -1);
        for (int v = 0; v < n; v++) if (indeg[v] == 0) dist[v] = 0;

        for (int u : order) {
            if (dist[u] == DagResult.INF) continue;
            for (Edge e : g.adj(u)) {
                m.edgeScans++;
                m.comparisons++;
                if (dist[u] + e.w < dist[e.v]) {
                    dist[e.v] = dist[u] + e.w;
                    parent[e.v] = u;
                }
            }
        }
        long sum = 0;
        for (long d : dist) if (d != DagResult.INF) sum += d;
        return new DagResult(dist, parent, sum);
    }
}
