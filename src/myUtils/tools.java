package myUtils;

import java.io.*;
import java.awt.*;
import java.applet.*;
import java.util.*;
import java.awt.event.*;
/**
 * Title:        db
 * Description:  Class for miscellaneous debugging features.
 * @author Jeff Sember
 * @version 1.0
 */
public class tools {

  public static String bool(boolean f) {
    return f ? "Y" : "N";
  }

	public static String chr(int code) {
    Character c = new Character((char)code);
    return c.toString();
	}

  public static boolean button1(MouseEvent e) {
    return ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK);
  }

  private tools() {}

  /**
   * Makes the current thread sleep for a specified time.  Ignores
   * any InterruptedExceptions that occur.
   * @param time time, in milliseconds, to sleep() for
   */
  public static void delay(int time) {
    try {
      Thread.sleep(time);
    }
    catch (InterruptedException e) {}
  }

  /**
   * Tests an assertion.  If the assertion is false, prints
   * an error message to System.out, and if init(Applet) was
   * called, to the browser status line.  Also prints a stack
   * trace to System.out, and finally calls System.exit(1).
   * @see #init(Applet)
   */
  public static void ASSERT(boolean flag, String message) {
    if (!flag) {
      System.out.println("ASSERTION FAILED: " + message);
      System.out.println(getStackTrace(3)); // print stack
      System.exit(1);
    }
  }

  public static void ASSERT(boolean flag) {
    if (!flag) {
      System.out.println("ASSERTION FAILED:");
      System.out.println(getStackTrace(3)); // print stack
      System.exit(1);
    }
  }

  /**
   * Returns a string representing x,y coordinates
   * @param x the x-coordinate
   * @param y the y-coordinate
   */
  public static String p2String(int x, int y) {
    String s;
    s = "(" + x + "," + y + ") ";
    return s;
  }

  public static int rnd(Random r, int val) {
    return math.mod(r.nextInt(),val);
  }
  public static boolean rndBool(Random r) {
    return r.nextInt() > 0;
  }

  /**
   * Returns a string representing a rectangle
   * @param x the x-coordinate of the top-left corner
   * @param y the y-coordinate of the top-left corner
   * @param w the width of the rectangle
   * @param h the height of the rectangle
   */
  public static String rString(int x, int y, int w, int h) {
    String s;
    s = "(Loc=" + x + "," + y + ", Size=" + w + "," + h + ") ";
    return s;
  }

  /**
   * Returns a string representing a rectangle
   * @param r the Rectangle
   */
  public static String rString(Rectangle r) {
    return r.toString();
  }

  // Displays a stack trace to System.out
  private static void dispStackTrace() {
    Throwable t = new Throwable();
    t.printStackTrace();
  }

  /** Constructs a string containing a stack trace.
      A debugging aid, used by the assert() method.

      @param trimAmount : number of calls to remove from
        front of string before returning

      @return A string describing the calls on the stack.
   */
  public static String getStackTrace(int trimAmount) {
//    if (DEBUG)
    {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      Throwable t = new Throwable();
      t.printStackTrace(new PrintStream(os));
      String s = os.toString();
      int start = 0;
      while (trimAmount-- > 0) {
        while (true) {
          if (start == s.length()) break;
          start++;
          if (s.charAt(start-1) == 0x0a) break;
        }
      }
      return s.substring(start);
    }
  }


  // gets a stack trace into a string
  public static String getStackTrace() {
    return getStackTrace(0);
/*    Throwable t = new Throwable(); // for getting stack trace
    ByteArrayOutputStream os = new ByteArrayOutputStream(); // for storing stack trace
    PrintStream ps = new PrintStream(os); // printing destination
    t.printStackTrace(ps);
    return os.toString();
    */
  }

  public static boolean warning(String s) {

    for (int i = 0; i < warningStrings.size(); i++) {
      if (s == warningStrings.get(i)) return false;
    }
    warningStrings.add(s);
    System.out.println("*** WARNING: "+s);
    return true;
  }
  private static DArray warningStrings = new DArray();
}