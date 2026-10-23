# DAA Assignment 3 — Graphs: DSU, MST, SCC and Shortest Paths

City Network & Job Scheduler. Java 17, Maven, JUnit 5. Own graph class (adjacency list), six algorithms
written from scratch: DSU, Kruskal, Prim, Kosaraju (iterative DFS), Kahn, shortest paths in a DAG.

## Layout
```
src/main/java/graph        Graph, Edge, GraphIO
src/main/java/algorithms   DSU, Kruskal, Prim, Kosaraju, Kahn, DagShortestPath (+ result classes)
src/main/java/metrics      Metrics (counters)
src/main/java/benchmark    Generator (exact Random(42) generator), Benchmark
src/test/java              JUnit 5 tests
data/                      disconnected.txt, cyclic.txt, dag.txt   (format: "n m directed|undirected", then "u v w")
results/results.csv        50 rows (5 algorithms x 10 graphs)
results/plots/             operations_vs_n.png, scans_per_edge.png, kruskal_edge_share.png
scripts/plots.py           builds the plots and prints the numbers used in REPORT.md
REPORT.md                  report
```

## Build, test, run
```
mvn test                                                   # all JUnit 5 tests
mvn -q compile exec:java -Dexec.mainClass=benchmark.Benchmark   # benchmark -> results/results.csv
python3 scripts/plots.py                                   # plots (needs matplotlib)
```
The benchmark is self-checking: it compares Kruskal and Prim weights, validates every topological order,
checks `Kosaraju = 2m`, `Kahn = m`, `DAG = m` edge scans and compares MST weight, SCC count and DAG distance sum
with the reference table. Any mismatch stops the run with an error, nothing is written to the CSV.
Same seed => the same counters on every machine.

Without Maven: `javac -d out $(find src/main -name '*.java') && java -cp out benchmark.Benchmark`.

## Counters
`dsuOps` = find calls + successful unions (counted inside `DSU`), `edgeScans` = edges looked at,
`comparisons` = weight comparisons (sort / priority queue / relaxation), `maxDepth` = deepest explicit DFS stack (Kosaraju).
Kahn and the DAG relaxation count only the scans of the main loop (in-degree initialisation is not counted), so both give exactly `m`.

## AI use acknowledgement
<!-- Edit this so it is true for you. -->
I used Claude (Anthropic) as an assistant for drafting the code structure, tests and report text.
I ran, read and checked everything myself and can explain every part of it at the defense.
