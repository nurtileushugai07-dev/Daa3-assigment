package algorithms;

import graph.Edge;
import java.util.List;

public class MstResult {
    public final long totalWeight;
    public final List<Edge> edges;
    public final int components;

    public MstResult(long totalWeight, List<Edge> edges, int components) {
        this.totalWeight = totalWeight;
        this.edges = edges;
        this.components = components;
    }
}
