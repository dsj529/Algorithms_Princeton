import java.util.Arrays;
import java.util.stream.Collectors;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private double[][] energy;
    private int[][] picRGB;
    private final static double MAX_ENERGY = 1000.0;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Can't submit null picture");
        }
        this.picRGB = getRGB(picture);
        energy = new double[picture.height()][picture.width()];
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                energy[y][x] = energy(x, y);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width(), height());
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                picture.setRGB(x, y, picRGB[y][x]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return picRGB[0].length;
    }

    // height of current picture
    public int height() {
        return picRGB.length;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return calcEnergy(x, y, picRGB);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return findSeam(transpose(picRGB), transpose(energy));
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findSeam(picRGB, energy);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {throw new IllegalArgumentException();}
        removeSeam(seam, transpose(energy), transpose(picRGB), true);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {throw new IllegalArgumentException();}
        removeSeam(seam, energy, picRGB, false);
    }

    // unit testing (optional)
    public static void main(String[] args) {
        Picture p = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(p);

        int[] s1 = sc.findVerticalSeam();
        sc.removeVerticalSeam(s1);
        int[] s2 = sc.findVerticalSeam();
        sc.removeVerticalSeam(s2);
        
        int[] s3 = sc.findHorizontalSeam();
        sc.removeHorizontalSeam(s3);

        String spam = "SPAM";
    }

    /***********
     * helper functions all below here.
     */

    private boolean validSeam(int[] seam, boolean b) {
        int h, w;
        if (b) {
            h = energy[0].length;
            w = energy.length;
        } else {
            h = energy.length;
            w = energy[0].length;
        }

        if (seam.length != h) {
            return false;
        }
        for (int r = 0; r < seam.length; r++) {
            if (seam[r] > w) {
                return false;
            }
            if (seam[r] < 0) {
                return false;
            }
            if (r < h - 1) {
                if (Math.abs(seam[r] - seam[r + 1]) > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeSeam(int[] seam, double[][] energyL, int[][] picRGBL, boolean b) {
        if (!validSeam(seam, b)) {
            throw new IllegalArgumentException();
        }
        int h = energyL.length;
        int w = energyL[0].length;
        if (w <= 1) {
            throw new IllegalArgumentException();
        }

        assert seam.length == h;
        double[][] newEnergy = new double[h][w - 1];
        int[][] newRGB = new int[h][w - 1];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w - 1; x++) {
                if (seam[y] <= x) {
                    newEnergy[y][x] = energyL[y][x + 1];
                    newRGB[y][x] = picRGBL[y][x + 1];
                } else {
                    newEnergy[y][x] = energyL[y][x];
                    newRGB[y][x] = picRGBL[y][x];
                }
            }
        }

        for (int i = 0; i < h; i++) {
            if (seam[i] > 0) {
                newEnergy[i][seam[i] - 1] = calcEnergy(seam[i] - 1, i, newRGB);
            }
            newEnergy[i][seam[i]] = calcEnergy(seam[i], i, newRGB);
        }

//        String outRow = null;
//        System.out.println("Old Matrix:");
//        for (int r = 0; r < h; r++) {
//            outRow = Arrays.stream(energyL[r]).mapToObj(String::valueOf).collect(Collectors.joining("||"));
//            System.out.println(outRow);
//        }
//        System.out.println("\nSeam:");
//        outRow = Arrays.stream(seam).mapToObj(String::valueOf).collect(Collectors.joining("||"));
//        System.out.println(outRow);
//
//        System.out.println("\nNew Matrix:");
//        for (int r = 0; r < h; r++) {
//            outRow = Arrays.stream(newEnergy[r]).mapToObj(String::valueOf).collect(Collectors.joining("||"));
//            System.out.println(outRow);
//        }
//
//        String spam = "SPAM";

        if (b) {
            energy = transpose(newEnergy);
            picRGB = transpose(newRGB);
        } else {
            energy = newEnergy;
            picRGB = newRGB;
        }

//        System.out.println("\nOld Matrix:");
//        for (int r = 0; r < h; r++) {
//            outRow = Arrays.stream(newEnergy[r]).mapToObj(String::valueOf).collect(Collectors.joining("||"));
//            System.out.println(outRow);
//        }
//        System.out.println("\nNew Matrix:");
//        for (int r = 0; r < energy.length; r++) {
//            outRow = Arrays.stream(energy[r]).mapToObj(String::valueOf).collect(Collectors.joining("||"));
//            System.out.println(outRow);
//        }
//        spam = "SPAM";
    }

    private int[] findSeam(int[][] picRGB, double[][] energy) {
        int h = picRGB.length;
        int w = picRGB[0].length;

        int[] seam = new int[h];
        double[][] distanceTo = new double[h][w];
        int[][] colTo = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                distanceTo[y][x] = Double.POSITIVE_INFINITY;
            }
        }

        for (int x = 0; x < w; x++) {
            distanceTo[0][x] = energy[0][x];
        }

        for (int i = 0; i < h - 1; i++) {
            for (int j = 0; j < w; j++) {
                colTo = relaxVertex(i, j, energy, distanceTo, colTo);
            }
        }

        double bestDist = Double.POSITIVE_INFINITY;
        for (int j = 0; j < w; j++) {
            if (bestDist > distanceTo[h - 1][j]) {
                bestDist = distanceTo[h - 1][j];
                seam[h - 1] = j;
            }
        }

        int curr = seam[h - 1];
        for (int i = h - 1; i > 0; i--) {
            seam[i - 1] = colTo[i][curr];
            curr = seam[i - 1];
        }
        return seam;
    }

    private int[][] relaxVertex(int i, int j, double[][] energy, double[][] distanceTo, int[][] colTo) {
        if (j > 0 && distanceTo[i + 1][j - 1] > (distanceTo[i][j] + energy[i + 1][j - 1])) {
            distanceTo[i + 1][j - 1] = (distanceTo[i][j] + energy[i + 1][j - 1]);
            colTo[i + 1][j - 1] = j;
        }
        if (distanceTo[i + 1][j] > (distanceTo[i][j] + energy[i + 1][j])) {
            distanceTo[i + 1][j] = (distanceTo[i][j] + energy[i + 1][j]);
            colTo[i + 1][j] = j;
        }
        if (j < (energy[0].length - 1) && distanceTo[i + 1][j + 1] > distanceTo[i][j] + energy[i + 1][j + 1]) {
            distanceTo[i + 1][j + 1] = (distanceTo[i][j] + energy[i + 1][j + 1]);
            colTo[i + 1][j + 1] = j;
        }
        return colTo;
    }

    private int[][] transpose(int[][] matrix) {
        int h = matrix.length;
        int w = matrix[0].length;
        int[][] matT = new int[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                matT[x][y] = matrix[y][x];
            }
        }
        return matT;
    }

    private double[][] transpose(double[][] matrix) {
        int h = matrix.length;
        int w = matrix[0].length;
        double[][] matT = new double[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                matT[x][y] = matrix[y][x];
            }
        }
        return matT;
    }

    private double calcEnergy(int x, int y, int[][] rgb) {
        if (x < 0 || y < 0 || x >= rgb[0].length || y >= rgb.length) {
            throw new IllegalArgumentException();
        }
        if (x == 0 || x == rgb[0].length - 1 || y == 0 || y == rgb.length - 1) {
            return MAX_ENERGY;
        }

        return Math.sqrt(gradient(x, y, 0, rgb) + gradient(x, y, 1, rgb));
    }

    private double gradient(int x, int y, int axis, int[][] rgb) {
        int n1, n2;
        if (axis == 0) {
            n1 = rgb[y][x - 1];
            n2 = rgb[y][x + 1];
        } else {
            n1 = rgb[y - 1][x];
            n2 = rgb[y + 1][x];
        }
        int r = ((n1 >> 16) & 0xff) - ((n2 >> 16) & 0xff);
        int g = ((n1 >> 8) & 0xff) - ((n2 >> 8) & 0xff);
        int b = ((n1 >> 0) & 0xff) - ((n2 >> 0) & 0xff);

        return r * r + g * g + b * b;
    }

    private int[][] getRGB(Picture p) {
        int[][] picRGB = new int[p.height()][p.width()];
        for (int y = 0; y < p.height(); y++) {
            for (int x = 0; x < p.width(); x++) {
                picRGB[y][x] = p.getRGB(x, y);
            }
        }
        return picRGB;
    }

}