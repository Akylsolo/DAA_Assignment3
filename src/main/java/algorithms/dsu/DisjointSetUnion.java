package algorithms.dsu;

import metrics.OperationMetrics;

/**
 * Disjoint Set Union (DSU) / Union-Find data structure.
 * Implements both:
 * 1) Path compression (halving or full compression)
 * 2) Union by rank
 * 
 * Provides makeSet, find, union, components.
 * Tracks find calls and successful union calls.
 */
public class DisjointSetUnion {
    private int[] parent;
    private int[] rank;
    private int components;
    private long findCount;
    private long successfulUnionCount;
    private OperationMetrics metrics;

    public DisjointSetUnion(int n) {
        this(n, null);
    }

    public DisjointSetUnion(int n, OperationMetrics metrics) {
        this.metrics = metrics;
        makeSet(n);
    }

    /**
     * Initializes n disjoint sets {0}, {1}, ..., {n-1}.
     */
    public void makeSet(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of elements cannot be negative: " + n);
        }
        this.parent = new int[n];
        this.rank = new int[n];
        for (int i = 0; i < n; i++) {
            this.parent[i] = i;
            this.rank[i] = 0;
        }
        this.components = n;
        this.findCount = 0;
        this.successfulUnionCount = 0;
    }

    /**
     * Finds the representative root of element x with path compression.
     */
    public int find(int x) {
        findCount++;
        if (metrics != null) {
            metrics.incDsuOps();
        }
        checkElement(x);
        // Iterative two-pass path compression (completely stack-safe even for n = 10^7)
        int root = x;
        while (root != parent[root]) {
            root = parent[root];
        }
        int curr = x;
        while (curr != root) {
            int next = parent[curr];
            parent[curr] = root;
            curr = next;
        }
        return root;
    }

    /**
     * Unites the sets containing elements x and y using union by rank.
     * @return true if elements were in different sets, false if already in same set.
     */
    public boolean union(int x, int y) {
        if (metrics != null) {
            metrics.incDsuOps();
        }
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        successfulUnionCount++;
        components--;
        return true;
    }

    /**
     * Directly unites two known roots (rootX, rootY) without additional find calls.
     */
    public boolean unionRoots(int rootX, int rootY) {
        if (metrics != null) {
            metrics.incDsuOps();
        }
        if (rootX == rootY) {
            return false;
        }
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        successfulUnionCount++;
        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int components() {
        return components;
    }

    public long getFindCount() {
        return findCount;
    }

    public long getSuccessfulUnionCount() {
        return successfulUnionCount;
    }

    public void setMetrics(OperationMetrics metrics) {
        this.metrics = metrics;
    }

    private void checkElement(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("Element index out of range [0, " + (parent.length - 1) + "]: " + x);
        }
    }
}
