import java.util.Stack;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;
    private int size;

    private class Node {
        Point2D data;
        double[] bounds;
        Node left;
        Node right;

        public Node(Point2D point, double[] bounds) {
            this.data = point;
            this.bounds = bounds;
            this.left = null;
            this.right = null;
        }
    }

    // construct an empty set of points
    public KdTree() {
        this.root = null;
        this.size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.root == null;
    }

    // number of points in the set
    public int size() {
        return this.size;
    }

    private double[] comparePts(Point2D p, Node n, int depth) {
        double[] diff = new double[2];
        if (depth % 2 == 0) {
            // p - n = {negative/left of n || positive/right of n}
            diff[0] = p.x() - n.data.x();
        } else {
            diff[0] = p.y() - n.data.y();
        }
        diff[1] = (p.x() == n.data.x() && p.y() == n.data.y()) ? 1 : 0;
        return diff;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't add null point to tree");
        }
        double[] bounds = { 0, 0, 1, 1 }; // xmin, ymin, xmax, ymax
        if (this.root == null) { // is this an empty tree?
            this.root = new Node(p, bounds);
            this.size++;
        } else {
            this.root = insert(this.root, p, bounds, 0);
        }
    }

    private Node insert(Node n, Point2D p, double[] bounds, int depth) {
        if (n == null) {
            this.size++;
            return new Node(p, bounds);
        } else {
            double[] diff = comparePts(p, n, depth);
            int chk = depth % 2;
            if (diff[1] == 1) {
                return n;
            }
            int d = diff[0] < 0 ? 0: 1;
            bounds[(2 * d + chk + 2) % 4] = (chk == 0) ? n.data.x() : n.data.y();
            // the modular arithmetic means that the boundary coordinates are cycled through
            // properly: { 2, 0, 3, 1 }
            if (d == 0) {
                n.left = insert(n.left, p, bounds, depth + 1);
            } else {
                n.right = insert(n.right, p, bounds, depth + 1);
            }
            return n;
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        return contains(root, p, 0) != null;
    }

    private Object contains(Node n, Point2D p, int depth) {
        if (n == null) {
            return null;
        }
        double[] diff = comparePts(p, n, depth);
        if (diff[1] == 1) {
            return n;
        } else {
            return contains(diff[0] < 0 ? n.left : n.right, p, depth + 1);
        }
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, 0);
    }

    private void draw(Node n, int depth) {
        if (n == null) {
            return;
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        StdDraw.point(n.data.x(), n.data.y());
        StdDraw.text(n.data.x(), n.data.y(), n.data.toString());
        if (depth % 2 == 0) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.data.x(), n.bounds[1], n.data.x(), n.bounds[3]);
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.bounds[0], n.data.y(), n.bounds[2], n.data.y());
        }
        draw(n.left, depth + 1);
        draw(n.right, depth + 1);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> inside = new Stack<Point2D>();
        range(root, rect, inside, 0);
        return inside;
    }

    private void range(Node n, RectHV r, Stack<Point2D> inside, int depth) {
        if (n == null) {
            return;
        }
        RectHV ptPane = new RectHV(n.bounds[0], n.bounds[1], n.bounds[2], n.bounds[3]);
        if (r.intersects(ptPane)) {
            if (r.contains(n.data)) {
                inside.push(n.data);
            }
            if (depth % 2 == 0) {
                if (n.bounds[2] >= r.xmin()) {
                    range(n.left, r, inside, depth + 1);
                }
                if (n.bounds[0] <= r.xmax()) {
                    range(n.right, r, inside, depth + 1);
                }
            } else {
                if (n.bounds[3] >= r.ymin()) {
                    range(n.left, r, inside, depth + 1);
                }
                if (n.bounds[1] <= r.ymax()) {
                    range(n.right, r, inside, depth + 1);
                }
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Can't search tree for null point");
        }
        if (isEmpty()) {
            return null;
        } else {
//            double closestDist = Double.MAX_VALUE;
//            Point2D closestPt = null;
//            closestPt = nearest(root, p, closestPt, closestDist, 0);
            Point2D closestPt = nearest(root, p, root.data, 0);
            return closestPt;
        }
    }

    private Point2D nearest(Node n, Point2D p, Point2D closestPt, int depth) {
        if (n == null) {
            return closestPt;
        }
        double[] cmp = comparePts(p, n, depth);

        if (cmp[1] == 1) {
            return p;
        }
        if (n.data.distanceTo(p) < closestPt.distanceTo(p)) {
            closestPt = n.data;
        }
        if (cmp[0] < 0) {
            closestPt = nearest(n.left, p, closestPt, depth + 1);
            if (closestPt.distanceSquaredTo(p) >= cmp[0]*cmp[0]) {
                closestPt = nearest(n.right, p, closestPt, depth+1);
            }
        } else {
            closestPt = nearest(n.right, p, closestPt, depth + 1);
            if (closestPt.distanceSquaredTo(p) >= cmp[0]*cmp[0]) {
                closestPt = nearest(n.left, p, closestPt, depth+1);
            }
        }
        return closestPt;
    }

//    private Point2D nearest(Node n, Point2D p, Point2D closestPt, double closestDist, int depth) {
//        if (n == null) {
//            return closestPt;
//        }
//
//        double currDist = rectDist(n, p);
//        if (currDist < closestDist) {
//            int[] diff = comparePts(p, n, depth);
//            if (diff[1] == 1) {
//                return n.data;
//            }
//            if (diff[0] == 0) { // p[i] < n[i] => scan left
//                closestPt = nearest(n.left, p, n.data, currDist, depth + 1);
//                if (n.right != null && currDist > rectDist(n.right, p)) {
//                    closestPt = nearest(n.right, p, n.data, currDist, depth + 1);
//                }
//            } else { // p[i] > n[i] => scan right
//                closestPt = nearest(n.right, p, n.data, currDist, depth + 1);
//                if (n.left != null && currDist > rectDist(n.left, p)) {
//                    closestPt = nearest(n.left, p, n.data, currDist, depth + 1);
//                }
//            }
//        }
//        return closestPt;
//    }
//
//    private double rectDist(Node n, Point2D p) {
//        RectHV rect = new RectHV(n.bounds[0], n.bounds[1], n.bounds[2], n.bounds[3]);
//        return rect.distanceSquaredTo(p);
//    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}
