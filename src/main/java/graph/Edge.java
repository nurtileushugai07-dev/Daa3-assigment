package graph;

/** One edge u -- v (undirected) or u -> v (directed) with an int weight. */
public final class Edge {
    public final int u, v, w;

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    /** The endpoint that is not {@code x} (for a self-loop returns x). */
    public int other(int x) {
        return x == u ? v : u;
    }
}
