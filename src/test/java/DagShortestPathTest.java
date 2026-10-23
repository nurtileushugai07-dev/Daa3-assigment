import algorithms.DagResult;
import algorithms.DagShortestPath;
import algorithms.Kahn;
import algorithms.TopoResult;
import graph.Edge;
import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathTest {
    /** Bellman-Ford with a virtual super-source (every in-degree-0 vertex starts at 0). */
    private static long[] bellmanFord(Graph g) {
        int n = g.n();
        long[] d = new long[n];
        Arrays.fill(d, DagResult.INF);
        boolean[] hasIn = new boolean[n];
        for (Edge e : g.edges()) hasIn[e.v] = true;
        for (int v = 0; v < n; v++) if (!hasIn[v]) d[v] = 0;
        for (int round = 0; round < n; round++)
            for (Edge e : g.edges())
                if (d[e.u] != DagResult.INF && d[e.u] + e.w < d[e.v]) d[e.v] = d[e.u] + e.w;
        return d;
    }

    @Test
    void matchesBellmanFordWithNegativeWeights() {
        Random rnd = new Random(13);
        for (int t = 0; t < 60; t++) {
            int n = 1 + rnd.nextInt(40);
            Graph g = TestGraphs.randomDag(n, rnd.nextInt(4 * n), rnd);
            TopoResult topo = Kahn.run(g, new Metrics());
            DagResult res = DagShortestPath.run(g, topo.order, new Metrics());
            long[] bf = bellmanFord(g);
            for (int v = 0; v < n; v++) assertEquals(bf[v], res.dist[v]);
        }
    }

    @Test
    void pathIsRestoredOnDataDag() throws Exception {
        Graph g = GraphIO.read(Path.of("data", "dag.txt"));
        TopoResult topo = Kahn.run(g, new Metrics());
        DagResult res = DagShortestPath.run(g, topo.order, new Metrics());
        // vertex 4 is best reached by 0 -> 2 (-3) -> 3 (+5) -> 4 (-1) = 1; vertex 2 = -3
        assertEquals(1L, res.dist[4]);
        assertEquals(List.of(0, 2, 3, 4), res.path(4));
        assertEquals(-3L, res.dist[2]);
    }

    @Test
    void scansExactlyM() {
        Graph g = TestGraphs.randomDag(300, 1200, new Random(9));
        TopoResult topo = Kahn.run(g, new Metrics());
        Metrics m = new Metrics();
        DagShortestPath.run(g, topo.order, m);
        assertEquals((long) g.m(), m.edgeScans);
    }
}
