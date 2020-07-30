package percolation;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] grid;
    private WeightedQuickUnionUF fullUF, auxUF;
    private int size, above, below, openSites;

    public Percolation(int n) {
        // create n-by-n grid, with all sites blocked
        if (n <= 0)
            throw new IllegalArgumentException("n needs to be > 0");

        this.size = n;
        this.grid = new boolean[n][n];
        this.fullUF = new WeightedQuickUnionUF(n * n + 2);
        this.auxUF = new WeightedQuickUnionUF(n * n + 1);
        this.above = n * n;
        this.below = n * n + 1;
        this.openSites = 0;
    }

    private void connectToVirtualEdges(int point) {
        if (point >= 0 && point < this.size) {
            // Connect virtual top to first row
            this.fullUF.union(this.above, point);
            this.auxUF.union(this.above, point);
        }

        if (point >= this.size * this.size - this.size && point < this.size * this.size) {
            // Connect virtual bottom to last row
            this.fullUF.union(this.below, point);
        }
    }

    private boolean indicesOnGrid(int row, int col) {
        return row > 0 && row <= this.size && col > 0 && col <= this.size;
    }

    private int xyTo1D(int row, int col) {
        return (this.size * (row - 1) + col - 1);
    }

    private void connectCells(int r1, int c1, int r2, int c2) {
        int p1 = this.xyTo1D(r1, c1);
        int p2 = this.xyTo1D(r2, c2);
        this.fullUF.union(p1, p2);
        this.auxUF.union(p1, p2);
    }

    private int[][] getNeighbors(int row, int col) {
        int neighbors[][] = { { row - 1, col }, { row + 1, col }, { row, col + 1 }, { row, col - 1 } };
        return neighbors;
    }

    public void open(int row, int col) {
        if (!this.indicesOnGrid(row, col))
            throw new java.lang.IndexOutOfBoundsException();
        if (this.isOpen(row, col))
            return;

        int pointToBeOpened = this.xyTo1D(row, col);

        this.grid[row - 1][col - 1] = true;
        this.openSites++;

        int[][] neighbors = getNeighbors(row, col);
        for (int[] neighbor : neighbors) {
            int nRow = neighbor[0];
            int nCol = neighbor[1];

            if (indicesOnGrid(nRow, nCol) && isOpen(nRow, nCol)) {
                connectCells(row, col, nRow, nCol);
            }
        }

        this.connectToVirtualEdges(pointToBeOpened);
    }

    public boolean isOpen(int row, int col) {
        if (!this.indicesOnGrid(row, col))
            throw new java.lang.IndexOutOfBoundsException();

        return this.grid[row - 1][col - 1];
    }

    public boolean isFull(int row, int col) {
        if (!this.indicesOnGrid(row, col))
            throw new java.lang.IndexOutOfBoundsException();

        return this.isOpen(row, col) && this.auxUF.connected(this.xyTo1D(row, col), this.above);
    }

    public int numberOfOpenSites() {
        return this.openSites;
    }

    public boolean percolates() {
        return this.fullUF.connected(this.above, this.below);
    }

    public static void main(String[] args) {
        System.out.println("Percolation");
    }
}