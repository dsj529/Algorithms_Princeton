import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private class SAPData {
        int ancestor;
        int dist;

        public SAPData(int v, int w) {
            BreadthFirstDirectedPaths a = new BreadthFirstDirectedPaths(graph, v);
            BreadthFirstDirectedPaths b = new BreadthFirstDirectedPaths(graph, w);
            compute(a, b);
        }

        public SAPData(Iterable<Integer> v, Iterable<Integer> w) {
            BreadthFirstDirectedPaths a = new BreadthFirstDirectedPaths(graph, v);
            BreadthFirstDirectedPaths b = new BreadthFirstDirectedPaths(graph, w);
            compute(a, b);
        }

        private void compute(BreadthFirstDirectedPaths a, BreadthFirstDirectedPaths b) {
            int closest = -1;
            int bestDist = -1;

            for (int i = 0; i < graph.V(); i++) {
                if (a.hasPathTo(i) && b.hasPathTo(i)) {
                    int thisDist = a.distTo(i) + b.distTo(i);
                    if (bestDist == -1 || thisDist < bestDist) {
                        closest = i;
                        bestDist = thisDist;
                    }
                }
            }
            ancestor = closest;
            dist = bestDist;
        }
    }

    private Digraph graph;
    private Map<String, SAPData> cache;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Must supply a valid graph");
        }
        graph = new Digraph(G);
        cache = new HashMap<>();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Indices must be within the graph");
        }
        return getData(v, w).dist;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Indices must be within the graph");
        }
        return getData(v, w).ancestor;

    }

    // length of shortest ancestral path between any vertex in v and any vertex in
    // w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Can't submit null lists");
        }
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Indices must be within the graph");
        }
        return getData(v, w).dist;

    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Can't submit null lists");
        }
        if (!validIndex(v) || !validIndex(w)) {
            throw new IllegalArgumentException("Indices must be within the graph");
        }
        return getData(v, w).ancestor;
    }

    private boolean validIndex(int n) {
        if (n < 0 || n >= graph.V()) {
            return false;
        }
        return true;
    }

    private boolean validIndex(Iterable<Integer> vertices) {
        for (Integer vertex : vertices) {
            if (vertex == null || !validIndex(vertex)) {
                return false;
            }
        }
        return true;
    }

    private SAPData getData(int v, int w) {
        String key = v + "_" + w;
        if (cache.containsKey(key)) {
            SAPData data = cache.get(key);
            cache.remove(key);
            return data;
        }

        SAPData data = new SAPData(v, w);
        cache.put(key, data);
        return data;
    }

    private SAPData getData(Iterable<Integer> v, Iterable<Integer> w) {
        String key = v.toString() + "_" + w.toString();
        if (cache.containsKey(key)) {
            SAPData data = cache.get(key);
            cache.remove(key);
            return data;
        }

        SAPData data = new SAPData(v, w);
        cache.put(key, data);
        return data;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
