"""Reads results/results.csv, writes 3 PNG plots to results/plots/ and prints the numbers for the report."""
import csv, math
from collections import defaultdict
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rows = list(csv.DictReader(open("results/results.csv")))
for r in rows:
    for k in ("n", "m", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"):
        r[k] = int(r[k])
    r["time_ms"] = float(r["time_ms"])
    r["dens"] = "sparse" if r["graph"].endswith("sparse") else "dense"

series = defaultdict(list)          # (algorithm, density) -> rows sorted by n
for r in rows:
    series[(r["algorithm"], r["dens"])].append(r)
for v in series.values():
    v.sort(key=lambda r: r["n"])

def slope(xs, ys):
    lx, ly = [math.log(x) for x in xs], [math.log(y) for y in ys]
    mx, my = sum(lx) / len(lx), sum(ly) / len(ly)
    return sum((a - mx) * (b - my) for a, b in zip(lx, ly)) / sum((a - mx) ** 2 for a in lx)

ALGS = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
COL = dict(zip(ALGS, ["tab:red", "tab:orange", "tab:blue", "tab:green", "tab:purple"]))
STY = {"sparse": "-", "dense": "--"}

def total_ops(r):
    return r["dsu_ops"] + r["edge_scans"] + r["comparisons"]

# plot 1: operations vs n
plt.figure(figsize=(7, 5))
for (a, d), v in series.items():
    plt.plot([r["n"] for r in v], [total_ops(r) for r in v], STY[d], marker="o", color=COL[a], label=f"{a} ({d})")
plt.xscale("log"); plt.yscale("log")
plt.xlabel("n (vertices)"); plt.ylabel("operations = dsu_ops + edge_scans + comparisons")
plt.title("Operations vs n"); plt.grid(True, which="both", alpha=.3); plt.legend(fontsize=7)
plt.tight_layout(); plt.savefig("results/plots/operations_vs_n.png", dpi=150); plt.close()

# plot 2: scans per edge
plt.figure(figsize=(7, 5))
for (a, d), v in series.items():
    plt.plot([r["n"] for r in v], [r["edge_scans"] / r["m"] for r in v], STY[d], marker="o", color=COL[a], label=f"{a} ({d})")
plt.xscale("log")
plt.xlabel("n (vertices)"); plt.ylabel("edge_scans / m")
plt.title("Scans per edge"); plt.grid(True, which="both", alpha=.3); plt.legend(fontsize=7)
plt.tight_layout(); plt.savefig("results/plots/scans_per_edge.png", dpi=150); plt.close()

# plot 3: share of edges Kruskal examines
plt.figure(figsize=(7, 5))
for d in ("sparse", "dense"):
    v = series[("Kruskal", d)]
    plt.plot([r["n"] for r in v], [r["edge_scans"] / r["m"] for r in v], STY[d], marker="o", label=f"Kruskal, {d} (m = {4 if d == 'sparse' else 20}n)")
plt.xscale("log")
plt.xlabel("n (vertices)"); plt.ylabel("edge_scans / m (Kruskal)")
plt.title("Share of edges Kruskal examines"); plt.grid(True, which="both", alpha=.3); plt.legend()
plt.tight_layout(); plt.savefig("results/plots/kruskal_edge_share.png", dpi=150); plt.close()

# ---- numbers for the report ----
print("== slopes of edge_scans vs n (log-log) ==")
for (a, d), v in sorted(series.items()):
    print(f"{a:16s}{d:7s} slope = {slope([r['n'] for r in v], [r['edge_scans'] for r in v]):.3f}")
print("\n== counter / formula ==")
def table(alg, label, f, key):
    for d in ("sparse", "dense"):
        v = series[(alg, d)]
        print(f"{alg:16s}{d:7s} {label:28s}", " ".join(f"{r[key] / f(r):.3f}" for r in v))
table("Kruskal", "dsu_ops / E", lambda r: r["m"], "dsu_ops")
table("Kruskal", "comparisons / (E log2 E)", lambda r: r["m"] * math.log2(r["m"]), "comparisons")
table("Prim", "comparisons / (E log2 E)", lambda r: r["m"] * math.log2(r["m"]), "comparisons")
table("Prim", "edge_scans / E", lambda r: r["m"], "edge_scans")
table("Kosaraju", "edge_scans / (V+E)", lambda r: r["n"] + r["m"], "edge_scans")
table("Kosaraju", "edge_scans / E", lambda r: r["m"], "edge_scans")
table("Kahn", "edge_scans / E", lambda r: r["m"], "edge_scans")
table("DagShortestPath", "edge_scans / E", lambda r: r["m"], "edge_scans")
print("\n== Kruskal share of edges ==")
for d in ("sparse", "dense"):
    print(d, " ".join(f"{r['edge_scans'] / r['m']:.3f}" for r in series[("Kruskal", d)]))
print("\n== Kruskal DSU ops/E vs n sizes:", [r["n"] for r in series[("Kruskal", "sparse")]])
print("\n== times at n=100000 (ms) ==")
for r in rows:
    if r["n"] == 100000: print(r["algorithm"], r["dens"], r["time_ms"])
print("\n== Kosaraju max_depth / n ==")
for d in ("sparse", "dense"):
    print(d, " ".join(f"{r['max_depth'] / r['n']:.3f}" for r in series[("Kosaraju", d)]))
