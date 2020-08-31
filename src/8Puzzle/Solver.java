import java.util.Comparator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private SearchNode goal;

    private class SearchNode {
        private final Board boardState;
        private final SearchNode prev;
        private final int moves;

        private SearchNode(Board board, SearchNode prev, int moves) {
            this.boardState = board;
            this.prev = prev;
            this.moves = moves;
        }
    }

    private class SearchOrder implements Comparator<SearchNode> {
        public int compare(SearchNode s1, SearchNode s2) {
            int m1 = s1.boardState.manhattan();
            int m2 = s2.boardState.manhattan();
            int priority1 = s1.moves + m1;
            int priority2 = s2.moves + m2;
            if (priority1 < priority2) {
                return -1;
            }
            if (priority1 > priority2) {
                return 1;
            }
            if (m1 < m2) {
                return -1;
            }
            if (m1 > m2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        if (!initial.canSolve()) {
            goal = null;
        } else {
            SearchOrder ordering = new SearchOrder();
            MinPQ<SearchNode> pq = new MinPQ<SearchNode>(ordering);
            MinPQ<SearchNode> twinPQ = new MinPQ<SearchNode>(ordering);

            SearchNode min = new SearchNode(initial, null, 0);

            // swap two tiles in the initial layout. by solving both initial and its twin in
            // tandem,
            // only one or the other should converge to a solution.
            SearchNode twinMin = new SearchNode(initial.twin(), null, 0);

            pq.insert(min);
            twinPQ.insert(twinMin);

            while (!min.boardState.isGoal() && !twinMin.boardState.isGoal()) {
                min = pq.delMin();
                for (Board b : min.boardState.neighbors()) {
                    if (min.prev == null || !b.equals(min.prev.boardState)) {
                        SearchNode n = new SearchNode(b, min, min.moves + 1);
                        pq.insert(n);
                    }
                }
                twinMin = twinPQ.delMin();
                for (Board b : twinMin.boardState.neighbors()) {
                    if (twinMin.prev == null || !b.equals(twinMin.prev.boardState)) {
                        SearchNode n = new SearchNode(b, twinMin, twinMin.moves + 1);
                        twinPQ.insert(n);
                    }
                }
            }
            if (min.boardState.isGoal()) {
                goal = min;
            } else if (twinMin.boardState.isGoal()) {
                goal = null;
            } else {
                goal = null;
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return goal != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) {
            return -1;
        } else {
            return goal.moves;
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        } else {
            Stack<Board> solutionPath = new Stack<Board>();
            for (SearchNode n = goal; n != null; n = n.prev) {
                solutionPath.push(n.boardState);
            }
            return solutionPath;
        }
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}