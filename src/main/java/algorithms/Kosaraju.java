package algorithms;

import graph.Edge;
import graph.Graph;
import metrics.Metrics;
import java.util.List;

public final class Kosaraju {
    private Kosaraju() {}

    public static SccResult run(Graph g, Metrics m) {
        if (!g.isDirected()) throw new IllegalArgumentException("SCC needs a directed graph");
        int n = g.n();
        int[] stack = new int[n];
        int[] next = new int[n];

        int[] order = new int[n];
        int[] cnt = {0};
        boolean[] visited = new boolean[n];
        for (int s = 0; s < n; s++) {
            if (!visited[s]) dfs(g, s, visited, stack, next, order, cnt, null, -1, m);
        }

        Graph r = g.reversed();
        boolean[] seen = new boolean[n];
        java.util.Arrays.fill(next, 0);
        int[] comp = new int[n];
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {
            int s = order[i];
            if (!seen[s]) {
                dfs(r, s, seen, stack, next, null, null, comp, count, m);
                count++;
            }
        }
        return new SccResult(count, comp);
    }

    private static void dfs(Graph g, int s, boolean[] visited, int[] stack, int[] next,
                            int[] finishOrder, int[] idx, int[] comp, int compId, Metrics m) {
        int sp = 0;
        stack[sp++] = s;
        visited[s] = true;
        if (comp != null) comp[s] = compId;
        if (sp > m.maxDepth) m.maxDepth = sp;
        while (sp > 0) {
            int v = stack[sp - 1];
            List<Edge> a = g.adj(v);
            if (next[v] < a.size()) {
                Edge e = a.get(next[v]++);
                m.edgeScans++;
                int w = e.v;
                if (!visited[w]) {
                    visited[w] = true;
                    if (comp != null) comp[w] = compId;
                    stack[sp++] = w;
                    if (sp > m.maxDepth) m.maxDepth = sp;
                }
            } else {
                if (finishOrder != null) finishOrder[idx[0]++] = v;
                sp--;
            }
        }
    }
}
