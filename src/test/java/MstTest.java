import algorithms.Kruskal;
import algorithms.MstResult;
import algorithms.Prim;
import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MstTest {
    @Test
    void kruskalAndPrimAgreeOn100RandomConnectedGraphs() {
        Random rnd = new Random(7);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60);
            Graph g = TestGraphs.randomConnected(n, rnd);
            MstResult k = Kruskal.run(g, new Metrics());
            MstResult p = Prim.run(g, new Metrics());
            assertEquals(k.totalWeight, p.totalWeight);
            assertEquals(n - 1, k.edges.size());
            assertEquals(n - 1, p.edges.size());
            assertEquals(1, k.components);
            assertEquals(1, p.components);
        }
    }

    @Test
    void disconnectedGraphGivesForestWithoutThrowing() throws Exception {
        Graph g = GraphIO.read(Path.of("data", "disconnected.txt")); // 8 vertices, 3 components, loop, parallel edges
        MstResult k = Kruskal.run(g, new Metrics());
        MstResult p = Prim.run(g, new Metrics());
        // {0,1,2}: 3+4 = 7 ; {3,4,5}: 1+2 = 3 ; {6} and {7} are isolated -> 4 components
        assertEquals(10L, k.totalWeight);
        assertEquals(10L, p.totalWeight);
        assertEquals(4, k.components);
        assertEquals(4, p.components);
        assertEquals(4, k.edges.size());
    }

    @Test
    void emptyAndSingleVertexGraphs() {
        assertEquals(0L, Kruskal.run(new Graph(1, false), new Metrics()).totalWeight);
        assertEquals(0L, Prim.run(new Graph(1, false), new Metrics()).totalWeight);
        assertEquals(0, Kruskal.run(new Graph(0, false), new Metrics()).components);
    }
}
