package myUtils;

import java.awt.*;
import java.util.Random;

public class math implements Globals {
  private static final double CIRCLE = Math.PI * 2;

  public static double distance(Point p1, Point p2) {
    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    return Math.sqrt(dx * dx + dy * dy);
  }

  public static final double sq(double a) {
    return a*a;
  }

	public static final boolean isNormal(double a) {

    return (a >= -Math.PI && a < Math.PI);
	}

	public static final double add180(double a) {
    return (a < 0) ? a + Math.PI : a - Math.PI;
	}

  public static double nonLinearInterpValue(double f) {

    double out;
    if (true) {
      out = Math.sin((f - .5) * Math.PI) / 2 + .5;
    } else {

    boolean flip = f >= .5;
    if (flip)
      f = 1.0 - f;

      final double accel = .4;
      if (f < accel) {
        out = accel - Math.sqrt(accel * accel - f*f);
      } else {
        out = f;
      }


      if (flip)
        out = 1.0 - out;
    }
    return out;
  }

  public static int grid(int x, double g) {
    int n = (int)((x+g/2) / g);
    return (int)(n * g);
  }

  public static int dotProduct(Point a0, Point a1, Point b0, Point b1) {
    return (a1.x - a0.x) * (b1.x - b0.x)
        + (a1.y - a1.y) * (b1.y - b0.y);
  }
  public static double degrees(double d) {
    return (d * (2*PI) / 360);
  }
  public static double rnd(double n) {
    double d = random.nextDouble();
    return d * n;

//    int n = mod(random.nextInt(),m);
  //  return n;
  }

  public static int crossProduct(Point a0, Point a1, Point b0, Point b1) {
    int x0 = a1.x-a0.x, x1 = b1.x - b0.x,
        y0 = a1.y - a0.y, y1 = b1.y - b0.y;
    return (x0 * y1) - (x1 * y0);
  }

  public static double round(double n) {
    return Math.round(n * 100) / 100.0;
  }

  public static String aStr(double a) {
    return Integer.toString( (int) (a * (180 / Math.PI)));
  }


  private static Random random = new Random(1965);

  public static void seed(int seed) {
    random = new Random(seed);
  }

  public static int rnd(int m) {
    int n = mod(random.nextInt(),m);
    return n;
  }

  public static int clamp(int value, int min, int max) {
    if (value < min)
      value = min;
    else if (value > max)
      value = max;
    return value;
  }
  public static double clamp(double value, double min, double max) {
    if (value < min)
      value = min;
    else if (value > max)
      value = max;
    return value;
  }


  public static boolean rectContainsRect(Rectangle rLarge, Rectangle rSmall) {
    return (
        rLarge.contains(rSmall.x, rSmall.y)
        &&
        rLarge.contains(rSmall.x + rSmall.width - 1,
                        rSmall.y + rSmall.height - 1));
  }

  public static double windAngle(double x, double y) {
    double ang = 0;
    if (y != 0 || x != 0) {
      if (y >= 0) {
        if (x >= 0) {
          ang = y / (x + y);
        }
        else {
          ang = 2 - (y / ( -x + y));
        }
      }
      else {
        if (x >= 0) {
          ang = y / (x - y);
        }
        else {
          ang = -2 + y / (x + y);
        }
      }
    }
    return ang * (Math.PI / 2);
  }

  public static void main(String[] args) {
    if (true) {
      for (int i = -900; i < 900; i+= 10) {
        double a = i * CIRCLE / 360;
        System.out.println("A="+Fmt.deg(a)+" Normal="+Fmt.deg(normalizeAngle(a)));
      }
      return;
    }

//  double a = 0.0;
    if (true) {
        for (int i = 0; i < 100; i++) {
          double f = i / 100.0;
          System.out.println("Linear: "+Fmt.f(f)+" <--> "+Fmt.f(nonLinearInterpValue(f)));
        }

    } else {
    for (int i = 0; i < 420; i += 15) {
      double a = i * (Math.PI / 180);
      double x = Math.cos(a) * 100;
      double y = Math.sin(a) * 100;
      double wa = windAngle(x, y);
      double at = Math.atan2(y, x);

      System.out.println("angle " + i + ", wa=" + (int) (wa * 1000) + " atan=" +
                         (int) (at * 1000) + " diff=" +
                         (int) (1000 * Math.abs(at - wa)));
    }
    }
  }

  /**
     Determine the position of a point c relative to a line from a to b.
     @param a     First point of line
     @param b     Second point of line
     @param c     Point to compare to line
     @return      0 if c is on the line; 1 if it's to the left; -1 if it's
                    to the right
   */
  public static int sideOfLine(Point a, Point b, Point c) {
    long area2 = (long) (b.x - a.x) * (c.y - a.y) -
        (long) (c.x - a.x) * (b.y - a.y);
    int pos = 0;
    if (area2 < 0)
      pos = -1;
    if (area2 > 0)
      pos = 1;
    return pos;
  }

  public static boolean inCone(Point origin, Point right, Point left,
                               Point test) {
    // Reflex vertex?

    boolean reflex = sideOfLine(origin, right, left) < 0;
    if (reflex) {
      return sideOfLine(origin, right, test) > 0
          || sideOfLine(origin, left, test) < 0;
    }
    else {
      return sideOfLine(origin, right, test) > 0
          && sideOfLine(origin, left, test) < 0;
    }
  }

  /**
   * Determine if one line segment (a,b) intersects another (c,d)
   * @param a
   * @param b
   * @param c
   * @param d
   * @return
   */
  public static boolean intersects(Point a, Point b, Point c, Point d) {
    return (sideOfLine(a, b, c) * sideOfLine(a, b, d) <= 0
            && sideOfLine(c, d, a) * sideOfLine(c, d, b) <= 0
            );
  }

  /**
   * Calculate the interior angle formed by three vertices.
   * @param a
   * @param b
   * @param c
   * @return the normalized angle between segments ab and ac.
   */
  public static double angle(Point a, Point b, Point c) {
    double a1 = Math.atan2(b.y - a.y, b.x - a.x);
    double a2 = Math.atan2(c.y - a.y, c.x - a.x);
    return normalizeAngle(a2-a1);
  }

  public static double polarAngle(double x, double y) {
    return normalizeAngle(Math.atan2(y,x));
  }

	public static void polarCoords(double angle, double radius, FPoint2 dest) {
dest.x = Math.cos(angle)*radius;
dest.y = Math.sin(angle)*radius;
	}

  /**
   *  Normalize an angle by replacing it, if necessary, with an
   * equivalent angle from -PI...+PI.
   *
   * @param a  angle to normalize
   * @return an equivalent angle from -PI...+PI
   */
  public static double normalizeAngle(double a) {

//    final double CIRCLE = Math.PI * 2;

    double b = mod(a + Math.PI, CIRCLE);

   // b -= CIRCLE * Math.floor(b / CIRCLE);
    /*
    boolean neg = b < 0;
    if (neg)
    	b = -b;
    b -= CIRCLE * Math.floor(b/CIRCLE);
    if (neg)
      b = -b;
      */
    b -= Math.PI;
//    tools.ASSERT(isNormal(b), "normalize "+a+" produced "+b);
    return b;
  }

  /**
   *  A better mod function.  This behaves as expected for negative
   *  numbers.  It has the following effect:  if the input value is
   *  negative, then enough multiples of the divisor are added to it
   *  to make it positive, then the standard mod function (%) is
   *  applied.
   *  @param value the number on the top of the fraction
   *  @param divisor the number on the bottom of the fraction
   *  @return the remainder of value / divisor.
   */
  public static int mod(int value, int divisor) {
    value = value % divisor;
    if (value < 0)
      value += divisor;
    return value;
  }

  /**
   *  A better mod function.  This behaves as expected for negative
   *  numbers.  It has the following effect:  if the input value is
   *  negative, then enough multiples of the divisor are added to it
   *  to make it positive, then the standard mod function (%) is
   *  applied.
   *  @param value the number on the top of the fraction
   *  @param divisor the number on the bottom of the fraction
   *  @return the remainder of value / divisor.
   */
  public static int mod(float value, float divisor) {
    return mod( (int) value, (int) divisor);
  }

  public static final double mod(double value, double divisor) {
    return (value - divisor * Math.floor(value / divisor));
  }

}