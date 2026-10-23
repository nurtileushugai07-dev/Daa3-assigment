import algorithms.DSU;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class DsuTest {
    @Test
    void findAgreesWithReferenceAfterRandomUnions() {
        Random rnd = new Random(1);
        int n = 300;
        DSU dsu = new DSU(n);
        int[] label = new int[n];                    // reference: relabel the whole set on every union
        for (int i = 0; i < n; i++) label[i] = i;
        int sets = n;
        for (int step = 0; step < 400; step++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            boolean merged = dsu.union(a, b);
            boolean ref = label[a] != label[b];
            assertEquals(ref, merged);
            if (ref) {
                int from = label[b], to = label[a];
                for (int i = 0; i < n; i++) if (label[i] == from) label[i] = to;
                sets--;
            }
            assertEquals(sets, dsu.components());
        }
        for (int a = 0; a < n; a++)
            for (int b = 0; b < n; b++)
                assertEquals(label[a] == label[b], dsu.find(a) == dsu.find(b));
    }

    @Test
    void countersWork() {
        DSU dsu = new DSU(4);
        dsu.union(0, 1);
        dsu.union(0, 1);        // second one is not successful
        assertEquals(1L, dsu.unionCalls);
        assertTrue(dsu.findCalls >= 4);
        assertEquals(3, dsu.components());
    }
}
