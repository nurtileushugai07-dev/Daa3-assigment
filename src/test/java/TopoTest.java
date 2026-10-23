import algorithms.Kahn;
import algorithms.TopoResult;
import graph.Graph;
import graph.GraphIO;
import metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class TopoTest {
    @Test
    void validOrderOnRandomDags() {
        Random rnd = new Random(11);
        for (int t = 0; t < 50; t++) {
            int n = 1 + rnd.nextInt(100);
            Graph g = TestGraphs.randomDag(n, rnd.nextInt(4 * n), rnd);
            TopoResult r = Kahn.run(g, new Metrics());
            assertFalse(r.hasCycle);
            assertEquals(n, r.count);
            assertTrue(Kahn.isValidOrder(g, r.order));
        }
    }

    @Test
    void validOrderOnDataDag() throws Exception {
        Graph g = GraphIO.read(Path.of("data", "dag.txt"));
        TopoResult r = Kahn.run(g, new Metrics());
        assertTrue(Kahn.isValidOrder(g, r.order));
    }

    @Test
    void cycleIsDetected() throws Exception {
        Graph g = GraphIO.read(Path.of("data", "cyclic.txt"));
        TopoResult r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
        assertTrue(r.count < g.n());
    }

    @Test
    void scansExactlyM() {
        Graph g = TestGraphs.randomDag(300, 1200, new Random(2));
        Metrics m = new Metrics();
        Kahn.run(g, m);
        assertEquals((long) g.m(), m.edgeScans);
    }
}
