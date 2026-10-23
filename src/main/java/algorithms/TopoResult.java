package algorithms;

public class TopoResult {
    public final int[] order;
    public final int count;
    public final boolean hasCycle;

    public TopoResult(int[] order, int count, boolean hasCycle) {
        this.order = order;
        this.count = count;
        this.hasCycle = hasCycle;
    }
}
