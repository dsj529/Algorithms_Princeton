import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private final static int CUTOFF = 15;
    private final int[] idx;
    private final int N;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        N = s.length();
        idx = new int[N];
        for (int i = 0; i < N; i++) {
            idx[i] = i;
        }
        sort(s, 0, N - 1, 0);
    }

    // length of s
    public int length() {
        return N;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= N) {
            throw new IllegalArgumentException();
        }
        return idx[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        int SCREEN_WIDTH = 80;
//        String s = StdIn.readString();
        String s = "I'm  your only friend I'm not your only friend but I'm a little glowing friend";
        int n = s.length();
        int digits = (int) Math.log10(n) + 1;
        String fmt = "%" + (digits == 0 ? 1 : digits) + "d ";
        StdOut.printf("String length: %d\n", n);
        CircularSuffixArray csa = new CircularSuffixArray (s);
        for (int i = 0; i < n; i++) {
            StdOut.printf(fmt, i);
            for (int j = 0; j < (SCREEN_WIDTH - digits - 1) && j < n; j++) {
                char c = s.charAt((j + csa.index(i)) % n);
                if (c == '\n')
                    c = ' ';
                StdOut.print(c);
            }
            StdOut.printf(fmt, csa.index(i));
            StdOut.println();
        }
    }

    /***
     * Private helper functions here
     */

    private char charAt(String s, int i, int o) {
        return s.charAt((i + o) % N);
    }

    private void sort(String s, int lo, int hi, int of) {
        if (hi - lo <= CUTOFF) {
            insertion(s, lo, hi, of);
            return;
        }

        int lt = lo, gt = hi, v = charAt(s, idx[lo], of), i = lo + 1;
        while (i < gt) {
            int t = charAt(s, idx[i], of);
            if (t < v) {
                exch(lt++, i++);
            } else if (t > v) {
                exch(i, gt--);
            } else {
                i++;
            }
        }
        sort(s, lo, lt - 1, of);
        sort(s, lt, gt, of + 1);
        sort(s, gt + 1, hi, of);
    }

    private void insertion(String s, int lo, int hi, int of) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(s, j, j - 1, of); j--) {
                exch(j, j - 1);
            }
        }
    }

    private boolean less(String s, int here, int there, int of) {
        int idx1 = idx[here], idx2 = idx[there];
        for (int cnt = of; cnt < N; cnt++) {
            if (charAt(s, idx1, cnt) < charAt(s, idx2, cnt)) {
                return true;
            }
            if (charAt(s, idx1, cnt) > charAt(s, idx2, cnt)) {
                return false;
            }
        }
        return false;
    }

    private void exch(int i, int j) {
        int swap = idx[i];
        idx[i] = idx[j];
        idx[j] = swap;
    }

}