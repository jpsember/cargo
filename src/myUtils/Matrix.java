package myUtils;

import java.util.*;

public class Matrix {

  private static void nl() {
    System.out.println();
  }

  public Matrix(double[] d, int height, int width, int start) {
    construct(d, height, width, start);
  }
public void display(String title) {
  System.out.println(title);
  display();
}

  public Matrix(int size) {
    construct(size, size);
  }

  public Matrix(int height, int width) {
    construct(height, width);
  }

  public void setLabelH(int i, String s) {
    hLabels[i] = s;
  }

  public void setLabelV(int i, String s) {
    vLabels[i] = s;
  }

  public String getLabelH(int i) {
    return hLabels[i];
  }

  public String getLabelV(int i) {
    return vLabels[i];
  }

  private void construct(int height, int width) {
    this.width = width;
    this.height = height;
    square = (width == height);
    vals = new double[width * height];
    hLabels = new String[width];
    vLabels = new String[height];

  }

  public void name(String s) {
    name = s;
  }

  public int width() {
    return width;
  }

  public int height() {
    return height;
  }

  public void displayLowerTri() {
    if (name != null) {
      System.out.println("Matrix: " + name);
    }
    for (int y = 0; y < height; y++) {
      System.out.print("[ ");
      for (int x = 0; x < width; x++) {
        if (x > y)
          System.out.print(Fmt.f());
        else
          printElement(y, x);
      }
      System.out.println(" ]");
    }
  }

  public void displayTriNW() {

    if (name != null) {
      System.out.println(name);
    }
    for (int y = height - 1; y >= 0; y--) {
      System.out.print("[ ");
      for (int x = 0; x <= y; x++) {
        //      printElement(y,x);
        System.out.print(Fmt.f( (int) (get(y, x))));
      }
      System.out.println(" ]");
    }
  }

  public void display(boolean labels) {
    if (name != null)
      System.out.println("Matrix: " + name);

      int w = 0;
    for (int y = 0; y < height; y++) {
      if (y == height-1 && labels) {
        for (int x = 0; x < w; x++)
        System.out.print("-");
        System.out.println();
      }
      if (labels && y == 0) {
        for (int x = 0; x < width; x++) {
          if (x == width-1)
            System.out.print(" ");
          System.out.print(Fmt.f(hLabels[x],-10));
          System.out.print(" ");
        }
        System.out.println();
        w = width * 11 + 4;
        for (int x = 0; x < w; x++)
          System.out.print("-");
          System.out.println();
      }
      if (labels) {
        System.out.print("  ");
      }
      else {
        System.out.print(Fmt.f(y, 3));
        System.out.print("[ ");
      }

      for (int x = 0; x < width; x++) {
        if (x == width-1)
          System.out.print("|");
        printElement(y, x);
        if (pivot && pivotI == y && pivotJ == x)
          System.out.print("*");
        else
          System.out.print(" ");

      }
      if (labels) {
        System.out.print("| = ");
        if (y < height - 1)
          System.out.print("-");
        System.out.print(vLabels[y]);
      }
      else
        System.out.print(" ]");
      System.out.print("\n");
    }
    System.out.print("\n");
    clearPivot();
  }

  public void display() {

    display(hLabels[0] != null);
  }

  public void clearPivot() {
    pivot = false;
  }

  public void setPivot(int i, int j) {
    pivot = true;
    pivotI = i;
    pivotJ = j;
  }

  private void printElement(int y, int x) {
    System.out.print(Fmt.f(get(y, x)));
  }

  private void construct(double[] d, int height, int width, int start) {
    construct(height, width);

    int j = start;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        set(y, x, d[j++]);
      }
    }
  }

  public void set(int y, int x, double v) {
   /* tools.ASSERT(y >= 0 && y < height && x >= 0 && x < width,
                 "set " + name() + " x" + x + " y" + y); */
    vals[y * width + x] = v;
  }

  public double get(int y, int x) {
/*    tools.ASSERT(y >= 0 && y < height && x >= 0 && x < width,
                 "get " + name() + " x" + x + " y" + y); */
    return vals[y * width + x];
  }

  private String name() {
    return (name != null ? name : "*** unnamed ***");
  }

  public static Matrix solveTridiagonal(Matrix m, Matrix b) {
/*    tools.ASSERT(m.square && m.height == b.height && b.width == 1,
                 "Bad dimensions");
*/
    Matrix x = new Matrix(b.height, 1);
    x.name("X");
    // Perform Crout factorization.

    Matrix l = new Matrix(m.height, m.width);
    l.name("L");
    Matrix u = new Matrix(m.height, m.width);
    u.name("U");
    Matrix z = new Matrix(m.height, 1);
    z.name("Z");
    u.setIdentityDiagonal();

    for (int i = 0; i < m.height; i++) {
      if (i > 0) {
        l.set(i, i - 1, m.get(i, i - 1));
        l.set(i, i, m.get(i, i) - l.get(i, i - 1) * u.get(i - 1, i));
      }
      else
        l.set(i, i, m.get(i, i));
      if (i < m.height - 1)
        u.set(i, i + 1, m.get(i, i + 1) / l.get(i, i));
      double zv = b.get(i, 0);
      if (i > 0)
        zv -= l.get(i, i - 1) * z.get(i - 1, 0);
      z.set(i, 0, zv / l.get(i, i));
    }

    for (int i = m.height - 1; i >= 0; i--) {
      double xv = z.get(i, 0);
      if (i < m.height - 1)
        xv -= u.get(i, i + 1) * x.get(i + 1, 0);
      x.set(i, 0, xv);
    }
    return x;
  }

  public void setIdentityDiagonal() {
  //  tools.ASSERT(square, "Not square!");
    for (int i = 0; i < height; i++)
      for (int j = 0; j < height; j++)
        set(i, j, i == j ? 1.0 : 0);
  }

  public static void copy(Matrix src, Matrix dest) {
    for (int i = src.vals.length - 1; i >= 0; i--)
      dest.vals[i] = src.vals[i];
  }

  public void setZ(int y, int x, double v) {
    if (Math.abs(v) < .00000001)
      v = 0;
    set(y,x,v);
  }

  private boolean pivot;
  private int pivotI, pivotJ;
  private String name;
  public double[] vals;
  private int height, width;
  private boolean square;
  private String[] hLabels, vLabels;
}