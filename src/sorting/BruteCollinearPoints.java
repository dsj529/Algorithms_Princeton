import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private int size;
    private LineSegment[] validSegments;

    private void testPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        Point[] checkPts = Arrays.copyOf(points, points.length);
        Arrays.sort(checkPts);

        for (int i = 0; i < checkPts.length; i++) {
            if (checkPts[i] == null) {
                throw new IllegalArgumentException();
            }
            if (i < checkPts.length - 1) {
                if (checkPts[i].compareTo(checkPts[i + 1]) == 0) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    public BruteCollinearPoints(Point[] points) {

        testPoints(points);
        validSegments = new LineSegment[2];

        for (int p = 0; p < points.length - 3; p++) {
            for (int q = p + 1; q < points.length - 2; q++) {
                double slopePQ = points[p].slopeTo(points[q]);
                for (int r = q + 1; r < points.length - 1; r++) {
                    double slopePR = points[p].slopeTo(points[r]);
                    if (slopePQ == slopePR) {
                        for (int s = r + 1; s < points.length; s++) {
                            double slopePS = points[p].slopeTo(points[s]);
                            if (slopePQ == slopePS) {
                                Point[] quartet = { points[p], points[q], points[r], points[s] };
                                Arrays.sort(quartet);
                                // add to list
                                enqueue(new LineSegment(quartet[0], quartet[3]));
                                quartet[0].drawTo(quartet[3]);
                                StdDraw.show();
                            }
                        }
                    }
                }
            }
        }
    }

    private void enqueue(LineSegment seg) {
        if (seg == null) {
            throw new IllegalArgumentException("Null Item cannot be added");
        }
        if (size == validSegments.length) {
            resize(2 * validSegments.length);
        }
        validSegments[size++] = seg;
    }

    private void resize(int capacity) {
        if (capacity < size) {
            throw new UnsupportedOperationException();
        }
        LineSegment[] newArr = new LineSegment[capacity];
        for (int i = 0; i < size; i++) {
            newArr[i] = validSegments[i];
        }
        validSegments = newArr;

    }

    public int numberOfSegments() {
        return size;
    }

    public LineSegment[] segments() {
        LineSegment[] out = new LineSegment[size];
        for (int i = 0; i < size; i++) {
            out[i] = validSegments[i];
        }
        return out;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];

        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        BruteCollinearPoints bcp = new BruteCollinearPoints(points);
        for (LineSegment seg : bcp.segments()) {
            StdOut.println(seg);
            seg.draw();
        }
        StdDraw.show();
    }
}
