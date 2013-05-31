package myUtils;

import java.awt.*;

public class Colors implements MeshConst {

  // Colors:
  public static int maxColors() {
    return maxColors;
  }

  private static int maxColors;
  private static Color[] colors;

  public static Color get(int hue) {
    return get(hue, MID_LEVEL);
  }

  public static Color get(int hue, int level) {
    return colors[hue * COLOR_LEVELS + math.clamp(level,0,COLOR_LEVELS-1)];
  }

  public static void set(Graphics g, int hue, int level) {
    g.setColor(get(hue,level));
  }

  public static void init() {
    if (colors != null) return;

    maxColors = COLOR_RECORDS;

    colors = new Color[maxColors * COLOR_LEVELS];
    // Prepare some default colors.
    final int[] def = {
      255, 255, 255,
      192, 192, 192,
      128, 128, 128,
      64, 64, 64,
      0, 0, 0,
      255, 0, 0,
      255, 175, 175,
      255, 200, 0,
      255, 255, 0,
      0, 255, 0,
      255, 0, 255,
      0, 255, 255,
      0, 0, 255,
      0x99,0x66,0x33,
      0x33,0x99,0xff,
      0x00,0x88,0x00,
    };

    for (int i = 0; i < def.length; i += 3) {
      add(i/3, def[i+0],def[i+1],def[i+2]);
    }

    // Add transition colors.
    final int[] trans = {
        BLUE,RED,
        RED,DARKGREEN,
        DARKGREEN,BLUE,
        GRAY,RED,
        GRAY,DARKGREEN,
    };
    for (int i = 0, j = BLUERED; i < trans.length; i += 2, j++) {
      int s = trans[i+0] * 3;
      int d = trans[i+1] * 3;
      addTransition(j, def[s+0], def[s+1], def[s+2],
                    def[d+0], def[d+1], def[d+2]);

    }

  }

  /**
   * Construct a Color object from rgb values; clamp them into
   * range beforehand
   *
   * @param r : red (0..255); clamped into range if necessary
   * @param g : green
   * @param b : blue
   * @return
   */
  public static Color construct(double r, double g, double b) {
    return new Color(math.clamp((int)r,0,255),
        math.clamp((int)g,0,255),
        math.clamp((int)b,0,255));

  }

  private static void add(int index, int shade, double r, double g, double b) {
    colors[index*COLOR_LEVELS + shade] = construct(r,g,b);
//        math.clamp((int)r,0,255),
//        math.clamp((int)g,0,255),
//        math.clamp((int)b,0,255));
  }

  public static void addTransition(int index, int r1, int g1, int b1,
                                   int r2, int g2, int b2) {
    for (int i = 0; i < COLOR_LEVELS; i++) {
      double s = (i+1) / (COLOR_LEVELS + 2.0);
      add(index,i,(r1 + (r2 - r1) * s),(g1 + (g2 - g1) * s),(b1 + (b2 - b1) * s));
    }
  }

  public static void add(int index, int r, int g, int b) {
    for (int i = 0; i < COLOR_LEVELS; i++) {
      double scale = (i * 2) / (double)COLOR_LEVELS;
      add(index,i,
          r * scale, g * scale, b * scale);
      /*
      int scale = (i << 8) / (COLOR_LEVELS / 2);
      int rs = (r * scale) >> 8;
      int gs = (g * scale) >> 8;
      int bs = (b * scale) >> 8;
      if (rs > 255)
        rs = 255;
      if (gs > 255)
        gs = 255;
      if (bs > 255)
        bs = 255;

      colors[index * COLOR_LEVELS + i] = new Color(rs, gs, bs);
          */
    }
  }

}