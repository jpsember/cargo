package myUtils;
import java.awt.Point;

public class Draw3D {
  private static final int SIZE = 4;

  public static void mult(Matrix ma, Matrix mb, Matrix md) {

    final double[] t = new double[SIZE*SIZE];
    double[] a = ma.vals, b = mb.vals, d = md.vals;
    int k = 0;
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        t[k++] = a[i*SIZE + 0] * b[0*SIZE + j]
             + a[i*SIZE + 1] * b[1*SIZE + j]
             + a[i*SIZE + 2] * b[2*SIZE + j]
             + a[i*SIZE + 3] * b[3*SIZE + j]
             ;
      }
    }

//System.out.println("Mult:");ma.display();mb.display();
    for (int i = SIZE*SIZE-1; i >=0; i--)
      d[i] = t[i];
  //    System.out.println("result:");md.display();
  }

  public static Matrix rotateX(double ang) {
    double c = Math.cos(ang),
        s = Math.sin(ang);
    Matrix m = identity();
    double[] v = m.vals;
    v[SIZE*1 + 1] = c;
    v[SIZE*1 + 2] = -s;
    v[SIZE*2 + 2] = c;
    v[SIZE*2 + 1] = s;
    return m;
  }
  public static Matrix rotateZ(double ang) {
    double c = Math.cos(ang),
        s = Math.sin(ang);
    Matrix m = identity();
    double[] v = m.vals;
    v[SIZE*0 + 0] = c;
    v[SIZE*0 + 1] = -s;
    v[SIZE*1 + 0] = c;
    v[SIZE*1 + 1] = s;
    return m;
  }
  public static Matrix rotateY(double ang) {
    double c = Math.cos(ang),
        s = Math.sin(ang);
    Matrix m = identity();
    double[] v = m.vals;
    v[SIZE*2 + 2] = c;
    v[SIZE*2 + 0] = -s;
    v[SIZE*0 + 0] = c;
    v[SIZE*0 + 2] = s;
    return m;
  }

  public static Matrix identity() {
    Matrix m = new Matrix(SIZE);
    double[] v = m.vals;
    v[0] = v[1*SIZE+1] = v[2*SIZE+2] = v[3*SIZE+3] = 1.0;
    return m;
  }

  public static Matrix translationMatrix(FPoint3 pt, boolean neg) {
    Matrix m = identity();
    double[] v = m.vals;
    double sign = neg ? -1 : 1;
    v[SIZE*0+SIZE-1] =pt.x * sign;
    v[SIZE*1+SIZE-1] =pt.y * sign;
    v[SIZE*2+SIZE-1] =pt.z * sign;
    return m;
  }

  public static void apply(Matrix m, FPoint3 pt, FPoint3 d) {
    double[] v = m.vals;
    d.x = v[SIZE*0 + 0] * pt.x
        + v[SIZE*0 + 1] * pt.y
        + v[SIZE*0 + 2] * pt.z
        + v[SIZE*0 + 3];
    d.y = v[SIZE*1 + 0] * pt.x
        + v[SIZE*1 + 1] * pt.y
        + v[SIZE*1 + 2] * pt.z
        + v[SIZE*1 + 3];
    d.z = v[SIZE*2 + 0] * pt.x
        + v[SIZE*2 + 1] * pt.y
        + v[SIZE*2 + 2] * pt.z
        + v[SIZE*2 + 3];
  }


  public static Matrix scaleMatrix(double s) {
    Matrix m = identity();
    m.set(0,0,s);
    m.set(1,1,s);
    m.set(2,2,s);
    return m;
  }
  public static void rotateY(Matrix ma, double ang)
  {
    Matrix m = Draw3D.rotateY(ang);
    mult(m,ma,ma);
  }
  public static void rotateX(Matrix ma, double ang)
  {
    Matrix m = Draw3D.rotateX(ang);
    mult(m,ma,ma);
  }
  public static void rotateZ(Matrix ma, double ang)
  {
    Matrix m = Draw3D.rotateZ(ang);
    mult(m,ma,ma);
  }
  public static void scale(Matrix m, double s) {
    double[] v = m.vals;
    v[0] *= s; v[1] *= s; v[2] *= s;
    v[4] *= s; v[5] *= s; v[6] *= s;
    v[8] *= s; v[9] *= s; v[10] *= s;
  }
  public static void scale(Matrix m, double sx, double sy, double sz) {
    double[] v = m.vals;
    v[0] *= sx; v[1] *= sx; v[2] *= sx;
    v[4] *= sy; v[5] *= sy; v[6] *= sy;
    v[8] *= sz; v[9] *= sz; v[10] *= sz;
   }
   public static void translate(Matrix m, double x, double y, double z) {
     double[] v = m.vals;
        v[0*SIZE + 3] += x;
        v[1*SIZE + 3] += y;
        v[2*SIZE + 3] += z;
      }

/*      public static void copyMat(Matrix src, Matrix dest) {
        for (int i = 0; i < SIZE*SIZE; i++)
          dest.vals[i] = src.vals[i];
      }
*/
}