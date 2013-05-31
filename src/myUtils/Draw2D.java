package myUtils;
//import java.awt.Point;

public final class Draw2D {
  public static Matrix rotationMatrix(double ang) {
    double c = Math.cos(ang),
        s = Math.sin(ang);
    Matrix m = new Matrix(3);
    double[] v = m.vals;
    v[0] = v[4] = c;
    v[1] = -s;
    v[3] = s;
    v[8] = 1;
    return m;
  }
  public static Matrix translationMatrix(double x, double y) {
    Matrix m = new Matrix(3);
    m.setIdentityDiagonal();
    m.set(0,2,x);
    m.set(1,2,y);
    return m;
  }

  public static FPoint2 apply(Matrix m, FPoint2 pt) {
    FPoint2 d = new FPoint2();
    apply(m,pt,d);
    return d;
  }

  public static void apply(Matrix m, double x, double y, FPoint2 d) {
    double[] v = m.vals;
    d.x = v[0] * x + v[1] * y + v[2];
    d.y = v[3] * x + v[4] * y + v[5];
  }
  public static void apply(Matrix m, FPoint2 pt, FPoint2 d) {
    apply(m,pt.x,pt.y,d);
  }


	public static Matrix mult(Matrix ma, Matrix mb) {
    Matrix md = Draw2D.identity();
    mult(ma,mb,md);
    return md;
	}

  public static void mult(Matrix ma, Matrix mb, Matrix md) {
    final double[] t = new double[9];
    double[] a = ma.vals, b = mb.vals, d = md.vals;
    t[0] = a[0]*b[0+0] + a[1] * b[3+0] + a[2] * b[6+0];
    t[1] = a[0]*b[0+1] + a[1] * b[3+1] + a[2] * b[6+1];
    t[2] = a[0]*b[0+2] + a[1] * b[3+2] + a[2] * b[6+2];

    t[3] = a[3]*b[0+0] + a[4] * b[3+0] + a[5] * b[6+0];
    t[4] = a[3]*b[0+1] + a[4] * b[3+1] + a[5] * b[6+1];
    t[5] = a[3]*b[0+2] + a[4] * b[3+2] + a[5] * b[6+2];

    t[6] = a[6]*b[0+0] + a[7] * b[3+0] + a[8] * b[6+0];
    t[7] = a[6]*b[0+1] + a[7] * b[3+1] + a[8] * b[6+1];
    t[8] = a[6]*b[0+2] + a[7] * b[3+2] + a[8] * b[6+2];

//System.out.println("Mult:");ma.display();mb.display();
    for (int i = 9-1; i >=0; i--)
      d[i] = t[i];
  //    System.out.println("result:");md.display();
      /*
    int k = 0;
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        d[k++] = a[j*3+0] * b[i*3+0]
             + a[j*3+1] * b[i*3+1]
             + a[j*3+2] * b[i*3+2];
       */
  }

	public static Matrix identity() {
    return scaleMatrix(1,1);
	}

  public static Matrix scaleMatrix(double xs, double ys) {
    Matrix m = new Matrix(3);
    m.set(0,0,xs);
    m.set(1,1,ys);
    m.set(2,2,1.0);
    return m;
  }

}