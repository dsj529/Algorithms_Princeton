import java.util.Arrays;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray_old {
    private final Integer[] idx;
    private final int n;

    // circular suffix array of s
    public CircularSuffixArray_old(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        
        n = s.length();
        idx = new Integer[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }

        Arrays.sort(idx, (Integer o1, Integer o2) -> {
            for (int i = 0; i < n; i++) {
                char c1 = s.charAt((o1 + 1) % n);
                char c2 = s.charAt((o2 + 1) % n);

                if (c1 < c2)
                    return -1;
                if (c1 > c2)
                    return 1;
            }
            return o1.compareTo(o2);
        });
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException();
        }
        return idx[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        int SCREEN_WIDTH = 80;
        String s = StdIn.readString();
        int n = s.length();
        int digits = (int) Math.log10(n) + 1;
        String fmt = "%" + (digits == 0 ? 1 : digits) + "d ";
        StdOut.printf("String length: %d\n", n);
        CircularSuffixArray_old csa = new CircularSuffixArray_old(s);
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
}