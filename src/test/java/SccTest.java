import algorithms.Kosaraju;
import algorithms.SccResult;
import graph.Edge;
import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class SccTest {
    /** reach[s][v] = v is reachable from s (BFS from every vertex). */
    private static boolean[][] reach(Graph g) {
        int n = g.n();
        boolean[][] r = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> q = new ArrayDeque<>();
            r[s][s] = true;
            q.add(s);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (Edge e : g.adj(u)) if (!r[s][e.v]) { r[s][e.v] = true; q.add(e.v); }
            }
        }
        return r;
    }

    @Test
    void matchesBruteForceOnRandomGraphs() {
        Random rnd = new Random(3);
        for (int t = 0; t < 40; t++) {
            int n = 1 + rnd.nextInt(200);
            Graph g = TestGraphs.randomDirected(n, rnd.nextInt(3 * n), rnd);
            SccResult res = Kosaraju.run(g, new Metrics());
            boolean[][] r = reach(g);
            for (int a = 0; a < n; a++)
                for (int b = 0; b < n; b++)
                    assertEquals(r[a][b] && r[b][a], res.component[a] == res.component[b]);
        }
    }

    @Test
    void cyclicDataFile() throws Exception {
        Graph g = GraphIO.read(Path.of("data", "cyclic.txt"));
        // components: {0,1,2}, {3,4,5}, {6}, {7}
        assertEquals(4, Kosaraju.run(g, new Metrics()).count);
    }

    @Test
    void scansExactly2m() {
        Graph g = TestGraphs.randomDirected(500, 2000, new Random(5));
        Metrics m = new Metrics();
        Kosaraju.run(g, m);
        assertEquals(2L * g.m(), m.edgeScans);
    }

    @Test
    void pathOf100000VerticesDoesNotOverflowTheStack() {
        int n = 100_000;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1, 1);
        Metrics m = new Metrics();
        SccResult res = assertDoesNotThrow(() -> Kosaraju.run(g, m));
        assertEquals(n, res.count);
        assertEquals(n, m.maxDepth);
    }
}
