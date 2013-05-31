package myUtils;

import java.awt.*;
import java.text.NumberFormat;

public class Fmt {

  /**
     Constructor.  Private, since no instances of this
     class can be instantiated.
   */
  private Fmt() {}

  public static String deg(double a) {
    return f( (int) ( (a * (180 / Math.PI))), 4);
  }

  public static void f(StringBuffer sb) {
    System.out.println(sb.toString());
    sb.setLength(0);
  }

  private static StringBuffer sb = new StringBuffer();
  private static ourNumFormat
  	fmtFP = new ourNumFormat(false),
    fmtINT = new ourNumFormat(true);

  /**
     Set format for displaying floating point numbers as strings.
     @param width : width of each string, including decimal point,
       negative sign, and digits
     @param fractionDigits : number of characters to reserve for
       digits to right of decimal point
   */
  public static void setNumberFormat(int width, int fractionDigits) {
    fmtFP.fmt.setMaximumFractionDigits(fractionDigits);
    fmtFP.fmt.setMinimumFractionDigits(fractionDigits);
    fmtFP.width = width;
  }

  /**
   * Format a string to be at least a certain size
   * @param s : string to format
   * @param length : minimum size to pad to; negative to
   *   insert leading spaces
   * @return blank-padded string
   */
  public static String f(String s, int length) {
    sb.setLength(0);
    if (length >= 0) {
      sb.append(s);
      return tab(sb, length).toString();
    }
    else {
      tab(sb, ( -length) - s.length());
      sb.append(s);
      return sb.toString();
    }
  }

  /**
     Set format for displaying integers as strings.
     @param width : width of each string
   */
  public static void setNumberFormat(int width) {
    fmtINT.width = width;
    fmtINT.fmt.setMaximumIntegerDigits(width);
  }

  /**
   * Display an array of doubles
   * @param arr : array to display
   * @param len : number of values to display; if negative, prints entire
   *   array
   * @param reversed : true to display in reversed order
   */
  public static void dispArray(double[] arr, int len, boolean reversed) {
    if (len < 0) {
      len = arr.length;
    }
    if (reversed) {
      for (int i = 0; i < len; i++) {
        System.out.print( (f(arr[len - 1 - i])));
      }
    }
    else {
      for (int i = 0; i < len; i++) {
        System.out.print(f(arr[i]));
      }
    }
  }

  public static void dispArray(double[] arr, int len) {
    dispArray(arr, len, false);
  }

  public static void dispArray(double[] arr) {
    dispArray(arr, arr.length);
  }

  /**
     Convert a double to a string, with appropriate formatting
     (align decimal place, and right justify)
     @param f : value to convert
     @return string containing value, set according to number format
   */
  private static String dblStr(double f) {
    return fmtStr(fmtFP.width, fmtFP.fmt.format(f));
  }

  public static String f(double f) {
    return dblStr(f);
  }

  public static String fa(double f) {
    return "undef";
  }

  public static String f() {
    StringBuffer sb = new StringBuffer();
    tab(sb, fmtFP.width);
    return sb.toString();
  }

  public static String f(Point p) {
    StringBuffer sb = new StringBuffer();
    sb.append('(');
    sb.append(f(p.x));
    sb.append(',');
    sb.append(f(p.y));
    sb.append(')');
    return sb.toString();
  }

  public static String f(int f) {
    return intStr(f);
  }

  public static String f(int val, int width, boolean spaceLeadZeros) {
    String s = f(val, width);
    if (!spaceLeadZeros) {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c == ' ') {
          c = '0';
        }
        sb.append(c);
      }
      s = sb.toString();
    }
    return s;
  }

  public static String f(int val, int width) {
    String s = fmtINT.fmt.format(val).trim();
    s = fmtStr(width, s);
    return s;
  }

  private static String fmtStr(int width, String num) {
    sb.setLength(0);
    tab(sb, width - num.length());
    sb.append(num);

    // Replace trailing decimal + zeros with spaces.
    {
      int j = sb.length() - 1;
      while (j >= 0) {
        if (sb.charAt(j) == '.') {
          while (j < sb.length()) {
            sb.setCharAt(j++, ' ');
          }
          break;

        }
        if (sb.charAt(j) != '0') {
          break;
        }
        j--;
      }
    }

    return sb.toString();
  }

  public static void add(StringBuffer sb, String s, int length) {
    int t = sb.length();
    sb.append(s);
    tab(sb, length + t);
  }

  /**
     Add spaces to a StringBuffer until its length is at
     some value.  Sort of a 'tab' feature, useful for
     aligning output.
     @param sb : StringBuffer to pad out
     @param len : desired length of StringBuffer; if
        it is already past this point, nothing is added to it
   */
  public static StringBuffer tab(StringBuffer sb, int len) {
    while (sb.length() < len) {
      sb.append(' ');
    }
    return sb;
  }

  /**
     Convert an integer to a right-justified string of fixed
     length (spaces added to left side).
     @param val : integer value to display in string
     @param len : desired length of string (if integer
        representation exceeds this amount, string is not
        truncated)
   */
  private static String intStr(int val) {
    return fmtStr(fmtINT.width, fmtINT.fmt.format(val));
  }

  public static void main(String[] args) {
    setNumberFormat(8, 3);
    System.out.println(">" + dblStr(Math.PI) + "#" + intStr(1965) + "#");
  }

}

class ourNumFormat {
  public ourNumFormat(boolean integer) {
    fmt = NumberFormat.getInstance();
    if (!integer) {
      fmt.setMaximumFractionDigits(2);
      fmt.setMinimumFractionDigits(2);
    } else {
      fmt.setGroupingUsed(false);
    }
		fmt.setMaximumIntegerDigits(6);

    width = 6;
  }

  public int width;
  public NumberFormat fmt;
}
