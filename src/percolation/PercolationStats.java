import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] x;

    public PercolationStats(int n, int trials) {
        // perform trials independent experiments on an n-by-n grid
        if (n <= 0 || trials <= 0) throw new java.lang.IllegalArgumentException("Pass n and t as arguments");

        this.x = new double[trials];

        for (int i = 0; i < trials; i++) {
//            System.out.println("Now performing trial " + i + "/" + trials);
            int openSitesWhenPercolates = this.performTrial(n);
            x[i] = (double)openSitesWhenPercolates / (n * n);
        }
    }

    private int performTrial(int n) {
        Percolation p = new Percolation(n);

        while (!p.percolates()) {
            int siteRow = StdRandom.uniform(1, n+1);
            int siteCol = StdRandom.uniform(1, n+1);

            if (!p.isOpen(siteRow, siteCol)) {
//                System.out.println("now opening (" + siteRow +", " + siteCol + ")");
                p.open(siteRow, siteCol);
            }
        }

        return p.numberOfOpenSites();
    }

    public double mean() {
        // sample mean of percolation threshold
        return StdStats.mean(this.x);
    }

    public double stddev() {
        // sample standard deviation of percolation threshold
        return StdStats.stddev(this.x);
    }

    public double confidenceLo() {
        // low  endpoint of 95% confidence interval
        return this.mean() - (1.96 * this.stddev() / Math.sqrt(this.x.length));
    }

    public double confidenceHi() {
        // high endpoint of 95% confidence interval
        return this.mean() + (1.96 * this.stddev() / Math.sqrt(this.x.length));
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        System.out.println("Performing " + trials + " trials on grids of side: " + n);

        PercolationStats pStats = new PercolationStats(n, trials);

        System.out.println("Mean                    = " + pStats.mean());
        System.out.println("Stddev                  = " + pStats.stddev());
        System.out.println("95% confidence interval = [" + pStats.confidenceLo() + ", " + pStats.confidenceHi() + "]");
    }
}