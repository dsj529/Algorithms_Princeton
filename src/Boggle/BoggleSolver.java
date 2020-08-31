package Boggle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieSET;

public class BoggleSolver {
    private TrieSet wordList;
    private TrieSET foundWords;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.wordList = new TrieSet();
        for (String word : dictionary) {
            if (word.length() >= 3) {
                wordList.add(word);
            }
        }
        this.foundWords = new TrieSET();
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        findWords(board);
        return foundWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int l = word.length();
        if (wordList.contains(word) && l >= 3) {
            if (l <= 4) {
                return 1;
            } else if (l == 5) {
                return 2;
            } else if (l == 6) {
                return 3;
            } else if (l == 7) {
                return 5;
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    /****
     * Private functions down here
     */

    private void findWords(BoggleBoard board) {

        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                boolean[][] visited = new boolean[board.rows()][board.cols()];
                dfs(board, r, c, "", visited);
            }
        }
    }

    private void dfs(BoggleBoard board, int r, int c, String oldWord, boolean[][] visited) {
        String newWord;
        visited[r][c] = true;

        char nextChar = board.getLetter(r, c);
        if (nextChar == 'Q') {
            newWord = oldWord + "QU";
        } else {
            newWord = oldWord + nextChar;
        }

        if (wordList.containsPrefix(newWord)) {
            if (newWord.length() >= 3 && wordList.isWord(newWord)) {
                foundWords.add(newWord);
            }

            for (int j = -1; j <= 1; j++) {
                if (c + j < 0 || c + j >= board.cols()) {
                    continue;
                }
                for (int i = -1; i <= 1; i++) {
                    if (r + i < 0 || r + i >= board.rows()) {
                        continue;
                    }
                    if (visited[r + i][c + j]) {
                        continue;
                    }
                    dfs(board, r + i, c + j, newWord, visited);
                }
            }
        }
        visited[r][c] = false;
    }
}