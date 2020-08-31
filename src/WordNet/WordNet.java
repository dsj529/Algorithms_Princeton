import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

public class WordNet {
    private final Map<Integer, String> idMap;
    private final Map<String, Set<Integer>> nounMap;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        idMap = new HashMap<>();
        nounMap = new HashMap<>();
        readSynsets(synsets);
        Digraph dg = readHypernyms(hypernyms);

        DirectedCycle cycle = new DirectedCycle(dg);
        if (cycle.hasCycle()) {
            throw new IllegalArgumentException("Cannot include cycles in wordnet data");
        }

        if (!rootedDAG(dg)) {
            throw new IllegalArgumentException("WordNet must be rooted");
        }

        sap = new SAP(dg);
    }

    private boolean rootedDAG(Digraph dg) {
        int r = 0;
        for (int i = 0; i < dg.V(); i++) {
            if (dg.outdegree(i) == 0) {
                r++;
                if (r > 1) {
                    return false;
                }
            }
        }
        return r == 1;
    }

    private Digraph readHypernyms(String hypernyms) {
        Digraph dg = new Digraph(idMap.size());
        In fileIn = new In(hypernyms);
        while (fileIn.hasNextLine()) {
            String[] line = fileIn.readLine().split(",");
            Integer id = Integer.valueOf(line[0]);
            for (int i = 1; i < line.length; i++) {
                Integer parent = Integer.valueOf(line[i]);
                dg.addEdge(id, parent);
            }
        }
        return dg;
    }

    private void readSynsets(String synsets) {
        In fileIn = new In(synsets);
        while (fileIn.hasNextLine()) {
            String[] line = fileIn.readLine().split(",");
            Integer id = Integer.parseInt(line[0]);
            String termSet = line[1];
            idMap.put(id, termSet);

            String[] nouns = termSet.split(" ");
            for (String noun : nouns) {
                Set<Integer> ids = nounMap.get(noun);
                if (ids == null) {
                    ids = new HashSet<>();
                }
                ids.add(id);
                nounMap.put(noun, ids);
            }
        }
    }

// returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null || word.equals("")) {
            return false;
        }
        return nounMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Must pass valid nouns");
        }

        Set<Integer> nA = nounMap.get(nounA);
        Set<Integer> nB = nounMap.get(nounB);
        return sap.length(nA, nB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA
    // and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {

        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Must pass valid nouns");
        }

        Set<Integer> nA = nounMap.get(nounA);
        Set<Integer> nB = nounMap.get(nounB);
        int ancestor = sap.ancestor(nA, nB);
        return idMap.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
    }
}