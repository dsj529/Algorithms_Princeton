import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
//        String s = BinaryStdIn.readString();
        String s = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        int N = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        for (int first = 0; first < N; first++) 
            if (csa.index(first) == 0) {
                BinaryStdOut.write(first);
                break;
            }
        
        for (int i = 0; i < N; i++) 
            BinaryStdOut.write(s.charAt((csa.index(i) + N - 1) % N));
        
        // print Burrows-Wheeler transform            
        BinaryStdOut.close();
    }
    
    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
//        int first = 3;
//        String t = "ARD!RCAAAABB";
        int N = t.length();     
        
        // construct the next array with Key-indexed counting
        int[] count = new int[R + 1], next = new int[N];
        for (int i = 0; i < N; i++)
            count[t.charAt(i) + 1]++;
        
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        
        for (int i = 0; i < N; i++) 
            next[count[t.charAt(i)]++] = i;
        
        // decode message
        for (int i = next[first], c = 0; c < N; i = next[i], c++) 
            BinaryStdOut.write(t.charAt(i));              
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected + or -\n");
        }
        String first = args[0];
        if (first.equals("+")) {
            inverseTransform();
        } else if (first.equals("-")) {
            transform();
        } else {
            throw new IllegalArgumentException("Unknown argument: " + first + "\n");
        }
    }

}