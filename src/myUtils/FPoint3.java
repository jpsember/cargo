package myUtils;

public class FPoint3 {
  public double x,y,z;
  public boolean equals(FPoint3 obj) {
    return x == obj.x && y == obj.y && z == obj.z;
  }

  public FPoint3(double x, double y, double z) {
    set(x,y,z);
  }
  public FPoint3() {}
  public void set(double x, double y, double z) {
    this.x = x; this.y = y; this.z = z;
  }
  public static void add(FPoint3 a, FPoint3 b, FPoint3 dest) {
    dest.x = a.x + b.x;
    dest.y = a.y + b.y;
    dest.z = a.z + b.z;
  }
  public static void interpolate(FPoint3 a, FPoint3 b, FPoint3 dest, double f, boolean nonLinear)
  {
    if (nonLinear)
      f = math.nonLinearInterpValue(f);

    dest.x = a.x + (b.x - a.x) * f;
    dest.y = a.y + (b.y - a.y) * f;
    dest.z = a.z + (b.z - a.z) * f;

  }

  public FPoint3(FPoint3 src) {
    set(src.x,src.y,src.z);
  }
  public void normalize() {
    double scale = 1 / Math.sqrt(x*x + y*y + z*z);
    x *= scale;
    y *= scale;
    z *= scale;
  }
  public static void crossProduct(FPoint3 s, FPoint3 t, FPoint3 dest) {
    dest.x = s.y * t.z - s.z * t.y;
    dest.y = s.z * t.x - s.x * t.z;
    dest.z = s.x * t.y - s.y * t.x;
  }
  public static void difference(FPoint3 b, FPoint3 a, FPoint3 d) {
    d.set(b.x - a.x, b.y - a.y, b.z - a.z);
  }

  public static void crossProduct(FPoint3 a, FPoint3 b, FPoint3 c, FPoint3 dest) {
    final FPoint3 s = new FPoint3();
    final FPoint3 t = new FPoint3();
    difference(b,a,s);
    difference(c,a,t);
    crossProduct(s,t,dest);
  }

  public static double dotProduct(FPoint3 s, FPoint3 t) {
    return s.x * t.x + s.y * t.y + s.z * t.z;
  }

  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.setLength(0);
    sb.append('(');
    sb.append(Fmt.f(x));
    sb.append(' ');
    sb.append(Fmt.f(y));
    sb.append(' ');
    sb.append(Fmt.f(z));
    sb.append(')');
    return sb.toString();
  }
}