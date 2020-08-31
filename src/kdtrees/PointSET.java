import java.util.Stack;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
    private TreeSet<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't add null point to tree");
        }
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't search tree for null point");
        }
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Can't search a null-ranged space");
        }
        Stack<Point2D> inside = new Stack<Point2D>();
        for (Point2D p : points) {
            if (rect.contains(p)) {
                inside.push(p);
            }
        }
        return inside;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't search tree for null point");
        }
        if (isEmpty()) {
            return null;
        } else {
            double closestDist = Double.MAX_VALUE;
            Point2D closestPt = null;

            for (Point2D q : points) {
                double ptDist = q.distanceTo(p);
                if (ptDist < closestDist) {
                    closestDist = ptDist;
                    closestPt = q;
                }
            }
            return closestPt;
        }
    }


    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}