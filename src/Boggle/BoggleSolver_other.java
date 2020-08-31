package Boggle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver_other {

    private static final int R = 26;
    private Set<String> words;
    private BoggleBoard board;
    private int[] letters;
    private ArrayList<Bag<Integer>> adj;
    private boolean[] onPath; // vertices in current path
    private final Node root;

    private static class Node {
        private String value; // word that ends on this node
        private Node[] next = new Node[R];
    }

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver_other(String[] dictionary) {
        this.root = new Node();
        // build Trie realization of String Symbal Table
        for (String word : dictionary) {
            // A valid word must contain at least 3 letters.
            if (word.length() >= 3)
                root.next[charToIndex(word.charAt(0))] = put(root.next[charToIndex(word.charAt(0))], word, 1);
        }
    }

    private Node put(Node node, String word, int d) {
        if (node == null)
            node = new Node();
        if (d == word.length()) {
            node.value = word;
            return node;
        }
        char c = word.charAt(d);
        int index = charToIndex(c);
        node.next[index] = put(node.next[index], word, d + 1);
        return node;
    }

    private int charToIndex(char c) {
        return c - 'A';
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard boggleBoard) {
        this.board = boggleBoard;
        this.words = new HashSet<>();

        // begin precompute the Boggle graph
        int v = board.rows() * board.cols();
        this.letters = new int[v];
        this.adj = new ArrayList<Bag<Integer>>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                int index = index(i, j);
                letters[index] = board.getLetter(i, j) - 'A';

                // add neighbors
                adj.add(index, new Bag<Integer>());
                for (int k = i - 1; k <= i + 1; k++) {
                    for (int m = j - 1; m <= j + 1; m++) {
                        if (k == i && m == j)
                            continue;
                        if (isValid(k, m))
                            adj.get(index).add(index(k, m));
                    }
                }
            }
        }
        // finish precompute the Boggle graph

        // DFS enumerate all simple paths in the Boggle graph
        for (int s = 0; s < v; s++) {
            for (int t = 0; t < v; t++) {
                if (s == t)
                    continue;
                // dfs find all path from s to t
                onPath = new boolean[v];
                dfs(s, t, root.next[letters[s]]);
            }
        }

        return this.words;
    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!contains(word))
            return 0;
        return scoreOfGen(word);
    }

    // helper functions
    private void dfs(int v, int t, Node x) {

        // if no prefix match then stop expanding
        if (x == null)
            return;

        // deal with special Qu case
        // if v stands for Q, then move to node 'U'
        if (letters[v] == 16)
            x = x.next[charToIndex('U')];

        if (x == null)
            return;

        // add v to current path
        onPath[v] = true;

        // found path from s to t
        if (v == t) {
            if (x.value != null)
                words.add(x.value);
        }

        // consider all neighbors that would continue path with repeating a node
        else {
            for (int w : adj.get(v)) {
                if (!onPath[w])
                    dfs(w, t, x.next[letters[w]]);
            }
        }

        // done exploring from v, so remove from path
        onPath[v] = false;
    }

    private int index(int i, int j) {
        return i * this.board.cols() + j;
    }

    private boolean isValid(int row, int col) {
        return (row >= 0) && (col >= 0) && (row < board.rows()) && (col < board.cols());
    }

    private int scoreOfGen(String word) {
        int len = word.length();
        int score;
        switch (len) {
        case 0:
        case 1:
        case 2:
            score = 0;
            break;
        case 3:
        case 4:
            score = 1;
            break;
        case 5:
            score = 2;
            break;
        case 6:
            score = 3;
            break;
        case 7:
            score = 5;
            break;
        default:
            score = 11;
            break;
        }
        return score;
    }

    private boolean contains(String word) {
        Node x = get(root, word, 0);
        return (x != null) && (x.value != null);
    }

    private Node get(Node x, String word, int d) {
        if (x == null)
            return null;
        if (d == word.length())
            return x;
        char c = word.charAt(d);
        return get(x.next[charToIndex(c)], word, d + 1);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver_other solver = new BoggleSolver_other(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}