import os
import csv
import numpy as np
import matplotlib.pyplot as plt

def main():
    csv_path = os.path.join("results", "results.csv")
    output_dir = os.path.join("results", "plots")
    os.makedirs(output_dir, exist_ok=True)

    rows = []
    with open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for r in reader:
            rows.append({
                "algorithm": r["algorithm"],
                "graph": r["graph"],
                "n": int(r["n"]),
                "m": int(r["m"]),
                "time_ms": float(r["time_ms"]),
                "dsu_ops": int(r["dsu_ops"]),
                "edge_scans": int(r["edge_scans"]),
                "comparisons": int(r["comparisons"]),
                "max_depth": int(r["max_depth"]),
                "result": int(r["result"]),
                "density": "sparse" if "sparse" in r["graph"] else "dense"
            })

    plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
    plt.rcParams.update({'font.sans-serif': 'DejaVu Sans', 'font.size': 10})

    algorithms = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
    colors = {
        "Kruskal": "#1f77b4",
        "Prim": "#ff7f0e",
        "Kosaraju": "#2ca02c",
        "Kahn": "#d62728",
        "DagShortestPath": "#9467bd"
    }

    fig, ax = plt.subplots(figsize=(10, 6), dpi=300)
    for algo in algorithms:
        for density, ls in [("sparse", "-"), ("dense", "--")]:
            data = [r for r in rows if r["algorithm"] == algo and r["density"] == density]
            data.sort(key=lambda x: x["n"])
            ns = [d["n"] for d in data]
            total_ops = [d["edge_scans"] + d["dsu_ops"] + d["comparisons"] for d in data]
            label = f"{algo} ({density})"
            ax.plot(ns, total_ops, marker='o' if density == "sparse" else 's',
                    linestyle=ls, color=colors[algo], label=label, linewidth=1.8, markersize=5)

    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("Number of Vertices $n$", fontsize=12, fontweight='bold')
    ax.set_ylabel("Total Operations (Scans + DSU + Comparisons)", fontsize=12, fontweight='bold')
    ax.set_title("Plot 1: Algorithm Operations vs Vertex Count $n$ (Log-Log Scale)", fontsize=14, fontweight='bold', pad=12)
    ax.legend(bbox_to_anchor=(1.04, 1), loc="upper left", frameon=True)
    plt.tight_layout()
    plot1_path = os.path.join(output_dir, "plot1_operations_vs_n.png")
    plt.savefig(plot1_path)
    plt.close()

    fig, ax = plt.subplots(figsize=(10, 6), dpi=300)
    for algo in algorithms:
        for density, ls in [("sparse", "-"), ("dense", "--")]:
            data = [r for r in rows if r["algorithm"] == algo and r["density"] == density]
            data.sort(key=lambda x: x["n"])
            ns = [d["n"] for d in data]
            ratio = [d["edge_scans"] / d["m"] for d in data]
            label = f"{algo} ({density})"
            ax.plot(ns, ratio, marker='o' if density == "sparse" else 's',
                    linestyle=ls, color=colors[algo], label=label, linewidth=1.8, markersize=5)

    ax.set_xscale("log")
    ax.set_xlabel("Number of Vertices $n$", fontsize=12, fontweight='bold')
    ax.set_ylabel("Scans per Edge ($edgeScans / m$)", fontsize=12, fontweight='bold')
    ax.set_title("Plot 2: Scans per Edge vs $n$ (Linear Work Validation)", fontsize=14, fontweight='bold', pad=12)
    ax.set_ylim(-0.1, 2.5)
    ax.axhline(2.0, color='gray', linestyle=':', alpha=0.6, label="Reference ratio 2.0 (Kosaraju)")
    ax.axhline(1.0, color='gray', linestyle='-.', alpha=0.6, label="Reference ratio 1.0 (Kahn/DSP)")
    ax.legend(bbox_to_anchor=(1.04, 1), loc="upper left", frameon=True)
    plt.tight_layout()
    plot2_path = os.path.join(output_dir, "plot2_scans_per_edge.png")
    plt.savefig(plot2_path)
    plt.close()

    fig, ax = plt.subplots(figsize=(9, 6), dpi=300)
    kruskal_sparse = [r for r in rows if r["algorithm"] == "Kruskal" and r["density"] == "sparse"]
    kruskal_dense = [r for r in rows if r["algorithm"] == "Kruskal" and r["density"] == "dense"]
    kruskal_sparse.sort(key=lambda x: x["n"])
    kruskal_dense.sort(key=lambda x: x["n"])

    ns_sp = [d["n"] for d in kruskal_sparse]
    share_sp = [d["edge_scans"] / d["m"] for d in kruskal_sparse]
    ns_dn = [d["n"] for d in kruskal_dense]
    share_dn = [d["edge_scans"] / d["m"] for d in kruskal_dense]

    ax.plot(ns_sp, share_sp, marker='o', color='#1f77b4', linewidth=2.5, markersize=7, label="Sparse ($m = 4n$)")
    ax.plot(ns_dn, share_dn, marker='s', color='#d62728', linewidth=2.5, markersize=7, label="Dense ($m = 20n$)")

    for x, y in zip(ns_sp, share_sp):
        ax.annotate(f"{y:.2f}", (x, y), textcoords="offset points", xytext=(0, 8), ha='center', fontsize=9)
    for x, y in zip(ns_dn, share_dn):
        ax.annotate(f"{y:.2f}", (x, y), textcoords="offset points", xytext=(0, -14), ha='center', fontsize=9)

    ax.set_xscale("log")
    ax.set_xlabel("Number of Vertices $n$", fontsize=12, fontweight='bold')
    ax.set_ylabel("Fraction of Edges Examined ($edgeScans / m$)", fontsize=12, fontweight='bold')
    ax.set_title("Plot 3: Share of Edges Kruskal Examines Before Early Exit", fontsize=14, fontweight='bold', pad=12)
    ax.set_ylim(0.0, 1.1)
    ax.legend(loc="center right", frameon=True, fontsize=11)
    plt.tight_layout()
    plot3_path = os.path.join(output_dir, "plot3_kruskal_edge_share.png")
    plt.savefig(plot3_path)
    plt.close()

    for algo in ["Kosaraju", "Kahn", "DagShortestPath", "Kruskal", "Prim"]:
        for density in ["sparse", "dense"]:
            data = [r for r in rows if r["algorithm"] == algo and r["density"] == density]
            data.sort(key=lambda x: x["n"])
            log_n = np.log10([d["n"] for d in data])
            log_scans = np.log10([d["edge_scans"] for d in data])
            slope, intercept = np.polyfit(log_n, log_scans, 1)

if __name__ == "__main__":
    main()
