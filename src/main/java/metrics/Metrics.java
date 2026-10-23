package metrics;

public class Metrics {
    public long dsuOps;       // find calls + successful unions
    public long edgeScans;    // edges looked at
    public long comparisons;  // weights compared
    public int maxDepth;      // deepest explicit DFS stack

    public void reset() {
        dsuOps = 0;
        edgeScans = 0;
        comparisons = 0;
        maxDepth = 0;
    }

    @Override
    public String toString() {
        return "dsuOps=" + dsuOps + " edgeScans=" + edgeScans
                + " comparisons=" + comparisons + " maxDepth=" + maxDepth;
    }
}
