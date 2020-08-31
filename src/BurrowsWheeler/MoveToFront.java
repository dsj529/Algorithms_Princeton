import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        char[] table = makeArray();
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            char tmpIn, count, tmpOut;
            for (count = 0, tmpOut = table[0]; ch != table[count]; count++) {
                tmpIn = table[count];
                table[count] = tmpOut;
                tmpOut = tmpIn;
            }
            table[count] = tmpOut;
            BinaryStdOut.write(count);
            table[0] = ch;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        char[] table = makeArray();
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            BinaryStdOut.write(table[ch], 8);
            char idx = table[ch];
            while (ch > 0) {
                table[ch] = table[ch--];
            }
            table[0] = idx;
        }
        BinaryStdOut.close();
    }

    private static char[] makeArray() {
        char[] table = new char[R];
        for (char i = 0; i < R; i++) {
            table[i] = i;
        }
        return table;
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected + or -\n");
        }
        String first = args[0];
        if (first.equals("+")) {
            decode();
        } else if (first.equals("-")) {
            encode();
        } else {
            throw new IllegalArgumentException("Unknown argument: " + first + "\n");
        }
    }

}