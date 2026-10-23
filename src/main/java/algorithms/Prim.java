package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public final class Prim {
    private Prim() {}

    private record Item(int to, Edge e) {}

    public static MstResult run(Graph g, Metrics m) {
        if (g.isDirected()) throw new IllegalArgumentException("MST needs an undirected graph");
        int n = g.n();
        boolean[] inTree = new boolean[n];
        PriorityQueue<Item> pq = new PriorityQueue<>((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.e().w, b.e().w);
        });
        List<Edge> tree = new ArrayList<>();
        long total = 0;
        int components = 0;

        for (int s = 0; s < n; s++) {
            if (inTree[s]) continue;
            components++;
            inTree[s] = true;
            push(g, s, inTree, pq, m);
            while (!pq.isEmpty()) {
                Item it = pq.poll();
                if (inTree[it.to()]) continue;   // vertex already in the tree
                inTree[it.to()] = true;
                tree.add(it.e());
                total += it.e().w;
                push(g, it.to(), inTree, pq, m);
            }
        }
        return new MstResult(total, tree, components);
    }

    private static void push(Graph g, int v, boolean[] inTree, PriorityQueue<Item> pq, Metrics m) {
        for (Edge e : g.adj(v)) {
            m.edgeScans++;
            int to = g.to(e, v);
            if (!inTree[to]) pq.add(new Item(to, e));
        }
    }
}
