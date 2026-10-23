package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import java.util.ArrayList;
import java.util.List;

public final class Kruskal {
    private Kruskal() {}

    public static MstResult run(Graph g, Metrics m) {
        if (g.isDirected()) throw new IllegalArgumentException("MST needs an undirected graph");
        int n = g.n();
        List<Edge> sorted = new ArrayList<>(g.edges());
        sorted.sort((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.w, b.w);
        });

        DSU dsu = new DSU(n);
        List<Edge> tree = new ArrayList<>();
        long total = 0;
        for (Edge e : sorted) {
            if (tree.size() == n - 1) break;
            m.edgeScans++;
            if (dsu.find(e.u) != dsu.find(e.v)) {
                dsu.union(e.u, e.v);
                tree.add(e);
                total += e.w;
            }
        }
        m.dsuOps += dsu.ops();
        return new MstResult(total, tree, dsu.components());
    }
}
