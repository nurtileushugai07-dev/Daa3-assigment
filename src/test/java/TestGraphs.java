import graph.Graph;
import java.util.Random;

/** Random graph helpers shared by the tests. */
final class TestGraphs {
    private TestGraphs() {}

    static Graph randomConnected(int n, Random rnd) {
        Graph g = new Graph(n, false);
        for (int i = 1; i < n; i++) g.addEdge(rnd.nextInt(i), i, 1 + rnd.nextInt(50));
        int extra = rnd.nextInt(2 * n);
        for (int k = 0; k < extra; k++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(50));
        return g;
    }

    static Graph randomDirected(int n, int m, Random rnd) {
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(20));
        return g;
    }

    /** Random DAG; weights may be negative. */
    static Graph randomDag(int n, int m, Random rnd) {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;
        for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int t = rank[i]; rank[i] = rank[j]; rank[j] = t; }
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            if (a == b) continue;
            if (rank[a] > rank[b]) { int t = a; a = b; b = t; }
            g.addEdge(a, b, rnd.nextInt(41) - 15);
        }
        return g;
    }
}
