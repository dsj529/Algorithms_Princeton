import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    private ArrayList<LineSegment> validSegments;

    private void testPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        Arrays.sort(points);

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
            if (i < points.length - 1) {
                if (points[i].compareTo(points[i + 1]) == 0) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    public FastCollinearPoints(Point[] points) {
        this.validSegments = new ArrayList<LineSegment>();
        /**
         * because the original point-array must stay unchanged by assignment
         * constraints, create two copies of the array: a) one as a master list sorted
         * in "natural" order, to be iterated over in the outer loop. b) one sorted in
         * slope-order relative to a point chosen in the master list, to be iterated
         * over in the inner loop.
         */
        Point[] workingPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(workingPoints);
        Point[] scanPoints = Arrays.copyOf(points, points.length);

        testPoints(workingPoints); // test for assignment assertions and throw exceptions if necessary.

        for (int o = 0; o < points.length; o++) { // walk the origin through the original set of points
            Arrays.sort(scanPoints, workingPoints[o].slopeOrder());
            ArrayList<Point> collinear = new ArrayList<Point>();
            double oldSlope = Double.NaN;
            Point p = scanPoints[0];
//            StdOut.println("Now scanning: " + p);
            collinear.add(p);
            for (int q = 1; q < points.length; q++) { // walk through the points as sorted relative to a point in the
                                                      // outer loop
                double slopePQ = p.slopeTo(scanPoints[q]);
                if (Double.isNaN(oldSlope) || oldSlope == slopePQ) {
                    collinear.add(scanPoints[q]);
                    oldSlope = slopePQ;
//                    StdOut.println(collinear);
                } else {
                    testForLine(collinear, p);
                    collinear = new ArrayList<Point>();
                    collinear.add(p);
                    collinear.add(scanPoints[q]);
                    oldSlope = slopePQ;
                }
            }
            testForLine(collinear, p);
        }
    }

    private void testForLine(ArrayList<Point> collinear, Point p) {
//        StdOut.println(collinear);
        if (collinear.size() >= 4) {
            Point[] aCollinear = collinear.toArray(new Point[0]);
            Arrays.sort(aCollinear);
//            StdOut.println("comparing " + aCollinear[0] + "to " + p);
//            StdOut.println(aCollinear[0].compareTo(p));
            if (aCollinear[0].compareTo(p) > -1) {
//                StdOut.println("Adding new segment");
                this.validSegments.add(new LineSegment(aCollinear[0], aCollinear[collinear.size() - 1]));
            }
        }
    }

    public int numberOfSegments() {
        return this.validSegments.size();
    }

    public LineSegment[] segments() {
        return this.validSegments.toArray(new LineSegment[0]);
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

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}