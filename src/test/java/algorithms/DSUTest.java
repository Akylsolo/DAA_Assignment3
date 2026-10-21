package algorithms;

import algorithms.dsu.DisjointSetUnion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DSUTest {

    static class ReferenceUF {
        private final int[] id;
        private int count;

        public ReferenceUF(int n) {
            this.id = new int[n];
            for (int i = 0; i < n; i++) id[i] = i;
            this.count = n;
        }

        public boolean connected(int p, int q) {
            return id[p] == id[q];
        }

        public void union(int p, int q) {
            int pId = id[p];
            int qId = id[q];
            if (pId == qId) return;
            for (int i = 0; i < id.length; i++) {
                if (id[i] == pId) id[i] = qId;
            }
            count--;
        }

        public int count() {
            return count;
        }
    }

    @Test
    @DisplayName("DSU agrees with reference implementation over random unions")
    void testDsuAgainstReference() {
        int n = 1000;
        int operations = 5000;
        Random rnd = new Random(12345);

        DisjointSetUnion dsu = new DisjointSetUnion(n);
        ReferenceUF ref = new ReferenceUF(n);

        assertEquals(n, dsu.components());
        assertEquals(n, ref.count());

        for (int op = 0; op < operations; op++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);

            boolean dsuUnion = dsu.union(u, v);
            boolean refConnectedBefore = ref.connected(u, v);

            assertEquals(!refConnectedBefore, dsuUnion, "Union return value mismatch");
            ref.union(u, v);

            assertEquals(ref.count(), dsu.components(), "Component count mismatch");
        }

        for (int i = 0; i < 2000; i++) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            assertEquals(ref.connected(u, v), dsu.connected(u, v));
            assertEquals(dsu.find(u) == dsu.find(v), ref.connected(u, v));
        }

        assertTrue(dsu.getFindCount() > 0);
        assertTrue(dsu.getSuccessfulUnionCount() > 0);
    }

    @Test
    @DisplayName("DSU handles edge cases: single element, bounds checking")
    void testEdgeCases() {
        DisjointSetUnion dsu = new DisjointSetUnion(1);
        assertEquals(1, dsu.components());
        assertEquals(0, dsu.find(0));
        assertFalse(dsu.union(0, 0));

        assertThrows(IndexOutOfBoundsException.class, () -> dsu.find(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.find(1));
        assertThrows(IllegalArgumentException.class, () -> new DisjointSetUnion(-5));
    }
}
