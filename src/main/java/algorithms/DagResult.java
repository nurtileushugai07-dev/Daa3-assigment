package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DagResult {
    public static final long INF = Long.MAX_VALUE / 4;
    public final long[] dist;
    public final int[] parent;   // -1 for sources
    public final long sum;       // sum of all finite distances

    public DagResult(long[] dist, int[] parent, long sum) {
        this.dist = dist;
        this.parent = parent;
        this.sum = sum;
    }

    public List<Integer> path(int v) {
        List<Integer> p = new ArrayList<>();
        for (int x = v; x != -1; x = parent[x]) p.add(x);
        Collections.reverse(p);
        return p;
    }
}
