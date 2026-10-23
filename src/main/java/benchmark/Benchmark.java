package benchmark;

import algorithms.*;
import graph.Graph;
import metrics.Metrics;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class Benchmark {
    static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    static final int[] DENSITY = {4, 20};

    static final long[] REF_MST = {147970, 30363, 468911, 97589, 1486139, 309550, 4715515, 968517, 15012094, 3056681};
    static final int[] REF_SCC = {27, 1, 137, 1, 373, 1, 1188, 1, 3923, 1};
    static final long[] REF_DAG = {396274, 252411, 1271911, 723157, 3941828, 2381342, 12551891, 7376612, 40096539, 23512087};

    private static class Timed<T> {
        T result;
        double medianMs;
    }

    private static <T> Timed<T> measure(Metrics m, Supplier<T> algo) {
        double[] t = new double[5];
        Timed<T> out = new Timed<>();
        for (int i = 0; i < 5; i++) {
            m.reset();
            long s = System.nanoTime();
            out.result = algo.get();
            t[i] = (System.nanoTime() - s) / 1e6;
        }
        Arrays.sort(t);
        out.medianMs = t[2];
        return out;
    }

    private static void check(boolean ok, String msg) {
        if (!ok) {
            System.err.println("CHECK FAILED: " + msg);
            throw new IllegalStateException(msg);
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> rows = new ArrayList<>();
        rows.add("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
        int idx = 0;
        for (int n : SIZES) {
            for (int d : DENSITY) {
                int m = d * n;
                String name = "n" + n + "_" + (d == 4 ? "sparse" : "dense");
                Graph ug = Generator.undirected(n, m);
                Graph dg = Generator.directed(n, m);
                Graph dag = Generator.dag(n, m);
                Metrics mm = new Metrics();

                Timed<MstResult> kr = measure(mm, () -> Kruskal.run(ug, mm));
                rows.add(row("Kruskal", name, n, m, kr.medianMs, mm, kr.result.totalWeight));
                check(kr.result.edges.size() == n - 1, name + ": Kruskal tree must have n-1 edges");
                check(kr.result.totalWeight == REF_MST[idx], name + ": Kruskal weight " + kr.result.totalWeight + " != " + REF_MST[idx]);

                Timed<MstResult> pr = measure(mm, () -> Prim.run(ug, mm));
                rows.add(row("Prim", name, n, m, pr.medianMs, mm, pr.result.totalWeight));
                check(pr.result.totalWeight == kr.result.totalWeight, name + ": Kruskal and Prim weights differ");

                Timed<SccResult> ko = measure(mm, () -> Kosaraju.run(dg, mm));
                rows.add(row("Kosaraju", name, n, m, ko.medianMs, mm, ko.result.count));
                check(ko.result.count == REF_SCC[idx], name + ": SCC count " + ko.result.count + " != " + REF_SCC[idx]);
                check(mm.edgeScans == 2L * m, name + ": Kosaraju edgeScans must be 2m");

                Timed<TopoResult> ka = measure(mm, () -> Kahn.run(dag, mm));
                rows.add(row("Kahn", name, n, m, ka.medianMs, mm, ka.result.count));
                check(!ka.result.hasCycle && ka.result.count == n, name + ": DAG must not have a cycle");
                check(Kahn.isValidOrder(dag, ka.result.order), name + ": invalid topological order");
                check(mm.edgeScans == m, name + ": Kahn edgeScans must be m");

                int[] order = ka.result.order;
                Timed<DagResult> ds = measure(mm, () -> DagShortestPath.run(dag, order, mm));
                rows.add(row("DagShortestPath", name, n, m, ds.medianMs, mm, ds.result.sum));
                check(ds.result.sum == REF_DAG[idx], name + ": DAG distance sum " + ds.result.sum + " != " + REF_DAG[idx]);
                check(mm.edgeScans == m, name + ": DAG edgeScans must be m");

                System.out.printf("%-14s OK  MST=%d  SCC=%d  DAGsum=%d%n", name,
                        kr.result.totalWeight, ko.result.count, ds.result.sum);
                idx++;
            }
        }
        Files.createDirectories(Path.of("results"));
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(Path.of("results", "results.csv")))) {
            rows.forEach(w::println);
        }
        System.out.println("All reference values match. Wrote results/results.csv (" + (rows.size() - 1) + " rows)");
    }

    private static String row(String alg, String graph, int n, int m, double ms, Metrics x, long result) {
        return String.format(java.util.Locale.ROOT, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                alg, graph, n, m, ms, x.dsuOps, x.edgeScans, x.comparisons, x.maxDepth, result);
    }
}
