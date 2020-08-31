import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final int[] board;
    private final int N;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.N = tiles[0].length;
        board = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[N * i + j] = tiles[i][j];
            }
        }
    }

    private Board(int[] tiles) {
        this.N = (int) Math.sqrt(tiles.length);
        this.board = Arrays.copyOf(tiles, tiles.length);
    }

    // string representation of this board
    public String toString() {
        String boardString = "" + N + "\n";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                boardString += "  " + board[N * i + j];
            }
            boardString += "\n";
        }
        return boardString;
    }

    // board dimension n
    public int dimension() {
        return N;
    }

    // number of tiles out of place
    public int hamming() {
        int h = 0;
        for (int i = 0; i < N * N; i++) {
            if (board[i] == i + 1 || board[i] == 0) {
                continue;
            }
            h++;
        }
        return h;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int m = 0;
        for (int i = 0; i < N * N; i++) {
            if (board[i] == i + 1 || board[i] == 0) {
                continue;
            }
            m += manhattan(board[i], i);
        }
        return m;
    }

    private int manhattan(int tile, int pos) {
        int row, col;
        row = Math.abs((tile - 1) / N - pos / N);
        col = Math.abs((tile - 1) % N - pos % N);
        return row + col;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < N * N - 1; i++) {
            if (board[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board other = (Board) y;
        if (other.N != this.N) {
            return false;
        }
        for (int i = 0; i < N * N - 1; i++) {
            if (other.board[i] != this.board[i]) {
                return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int pos = 0;
        while (board[pos] != 0 && pos < N * N) {
            pos++;
        }
        if (pos == N * N) {
            return null;
        }

        Queue<Board> newBoards = new Queue<Board>();
        if (pos / N != 0) { // skip on first row
            Board newBoard = swap(pos, pos - N);
            newBoards.enqueue(newBoard);
        }
        if (pos / N != N - 1) { // skip on last row
            Board newBoard = swap(pos, pos + N);
            newBoards.enqueue(newBoard);
        }
        if (pos % N != 0) { // skip on first column
            Board newBoard = swap(pos, pos - 1);
            newBoards.enqueue(newBoard);
        }
        if (pos % N != N - 1) { // skip on last col
            Board newBoard = swap(pos, pos + 1);
            newBoards.enqueue(newBoard);
        }
        return newBoards;

    }

    private Board swap(int t1, int t2) {
        int[] tiles = Arrays.copyOf(board, N * N);
        int temp = tiles[t2];
        tiles[t2] = tiles[t1];
        tiles[t1] = temp;
        return new Board(tiles);
    }

    // a board that is obtained by exchanging any pair of tiles on the same row
    public Board twin() {
        int x, y;
        if (N == 1) {
            return null;
        }
        if (board[0] != 0 && board[1] != 0) {
            x = 0;
            y = 1;
        } else {
            x = N;
            y = N + 1;
        }
        return swap(x, y);
    }
    
    public boolean canSolve() {
        int zeroRow = 0;
        int inversions = 0;
        for (int i = 0; i < N * N; i++) {
            if (board[i] == 0) {
                zeroRow = i % N;
            }
            if (i < N * N - 1 && i / N == (i + 1) / N) {
                if (board[i] > board[i + 1] && board[i]!=0 && board[i+1]!=0) {
                    inversions++;
                }
            }
        }
        if (N % 2 == 1) {
            // count inversions, check if odd
            return inversions % 2 == 0;
        } else {
            // count inversions + zero row
            return (inversions + zeroRow) % 2 == 1;
        }
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        StdOut.println("Initial state:\n" + initial);
//        StdOut.println("Can Solve: " + canSolve());
        StdOut.println("Neighbors:");
        for (Board b : initial.neighbors()) {
            StdOut.println(b);
        }
    }
}
