import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wn;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wn = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String out = null;
        int max = 0;

        for (String nounA : nouns) {
            int thisDist = 0;
            for (String nounB : nouns) {
                if (!nounA.equals(nounB)) {
                    thisDist += wn.distance(nounA, nounB);
                }
            }
            if (thisDist > max) {
                max = thisDist;
                out = nounA;
            }
        }
        return out;
    }

    // see test client below
    public static void main(String[] args) {
        long start = System.nanoTime();
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.isNoun("civil_service"));
        System.out.println(wordnet.isNoun("family_Fissurellidae"));
        Outcast outcast = new Outcast(wordnet);

        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
        long end = System.nanoTime();
        System.out.println("Duration: " + (end - start) / 1000000);
    }
}