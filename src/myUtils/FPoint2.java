package myUtils;
import java.awt.Point;

public class FPoint2 {
    public double x,y;

    public FPoint2(double x, double y) {
      set(x,y);
    }
    public Point point() {
      return new Point((int)x, (int)y);
    }
		public void set(FPoint2 src) {
      set(src.x,src.y);
		}
    public void add(FPoint2 b) {
      x += b.x; y+=b.y;
    }
    public void add(double x, double y) {
      this.x += x;
      this.y += y;
    }

    public boolean equals(FPoint2 obj) {
      return x == obj.x && y == obj.y;
    }
	public static FPoint2 pt(double x, double y) {return new FPoint2(x,y);}

    public double distanceFrom(FPoint2 pt) {
      return distance(x-pt.x, y-pt.y);
    }
		public static double distance(double dx, double dy) {
      return Math.sqrt(dx*dx + dy*dy);
		}

  public static double distance(FPoint2 p1, FPoint2 p2) {
    return p2.distanceFrom(p1);
  }

    public void applyGrid(int size) {
     // tools.ASSERT(size > 0);
      int ix = (int)(x + size/2), iy = (int)(y + size/2);
      ix -= math.mod(ix,size);
      iy -= math.mod(iy,size);
      x = ix;
      y = iy;
    }

    public FPoint2() {}
    public void set(double x, double y) {
      this.x = x; this.y = y;
    }
    public static void add(FPoint2 a, FPoint2 b, FPoint2 dest) {
      dest.x = a.x + b.x;
      dest.y = a.y + b.y;
    }

    public FPoint2(FPoint2 src) {
      set(src.x,src.y);
    }
    public void normalize() {
      double scale = 1 / Math.sqrt(x*x + y*y);
      x *= scale;
      y *= scale;
    }
    public static double crossProduct(FPoint2 s, FPoint2 t) {
      return s.x * t.y - s.y * t.x;
    }

    public static double dotProduct(FPoint2 s, FPoint2 t, FPoint2 dest) {
      return s.x * t.x + s.y * t.y;
    }

    public String toString(boolean dumpFlag) {
      final StringBuffer sb = new StringBuffer();
      sb.setLength(0);
      if (!dumpFlag)
      sb.append('(');
      sb.append(Fmt.f(x));
      sb.append(' ');
       sb.append(Fmt.f(y));
      if (!dumpFlag)
      sb.append(')');
      else
        sb.append(", ");
      return sb.toString();
    }

    public String toString() {
      return toString(false);
    }
}