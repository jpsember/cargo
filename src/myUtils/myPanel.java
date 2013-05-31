package myUtils;

import java.awt.*;
import java.awt.event.*;
// ----- ps
import java.io.*;

// -----
public class myPanel
  extends Panel
  implements MouseListener, MouseMotionListener,
  ComponentListener, Globals {

  /**
   * Draw a bevelled edge within the panel.  The edge is drawn within
   * a rectangle specified by x,y coordinate pairs.  The bottom and
   * right edges are not drawn.
   * @param g the graphics object
   * @param iBevelWidth the width of the bevelled edge
   * @param x0 left edge
   * @param y0 top edge
   * @param x1 right edge
   * @param y1 bottom edge
   * @param sunken true if edge is to be sunken, otherwise raised
   */
  private void drawBevelEdge(Graphics g, int iBevelWidth, int x0, int y0,
                             int x1, int y1, boolean sunken) {
    g.setColor(sunken ? colorDarker : colorLighter);
    for (int i = 0; i < iBevelWidth; i++) {
      g.drawLine(x0 + i, y0 + i, x1 - 1 - i, y0 + i);
      g.drawLine(x0 + i, y0 + i + 1, x0 + i, y1 - 2 - i);
    }
    g.setColor(sunken ? colorLighter : colorDarker);
    for (int i = 0; i < iBevelWidth; i++) {
      g.drawLine(x1 - 1 - i, y0 + i + 1, x1 - 1 - i, y1 - 1 - i);
      g.drawLine(x0 + i, y1 - 1 - i, x1 - 2 - i, y1 - 1 - i);
    }
  }

  /**
   * Create an array of Color[] from an array of rgb integers
   * @param size the number of (r,g,b) entries in the array
   * @param rgbArray an array of integers, sets of red, green, and blue
   */
  public static Color[] createColorTable(int rgbArray[]) {
    int size = rgbArray.length / 3;
    Color colArray[] = new Color[size];
    for (int i = 0; i < size; i++) {
      int j = i * 3;
      colArray[i] = new Color(rgbArray[j], rgbArray[j + 1], rgbArray[j + 2]);
    }
    return colArray;
  }

  /*
   public Rectangle getInteriorRect() {
      if (rInterior == null)
      calculateInteriorSize();
      return rInterior;
   }
   */

  /**
   * Determine the size of the interior of the panel
   */
  public Point getInteriorSize() {
    if (rInterior == null) {
      calculateInteriorSize();
    }
    return new Point(rInterior.width, rInterior.height);
  }

  public double aspectRatio() {
    Point pt = getInteriorSize();
    return pt.y / (double) pt.x;
  }

  public Point centerRect(int width, int height) {
    return new Point( (rInterior.width - width) / 2,
                     (rInterior.height - height) / 2);
  }

  /**
   * Return the Insets of the panel
   */
  public Insets insets() {
    return insetsInterior;
  }

  public void setColor(Color c) {
    if (c != null) {
      graf.setColor(c);
    }
    prevColor = c;
  }

  private Color prevColor;
  public Color getColor() {
    return prevColor;
  }

  public myPanel(String title, int iStyle, Color c) {
    init(title, iStyle, c);
  }

  public myPanel() {
    init(null, STYLE_PLAIN, null);
  }

  public Point getMousePt(MouseEvent e) {
    return getMousePtF(e).point();
    // return panelToWorldI(e.getPoint());
  }

  public FPoint2 getMousePtF(MouseEvent e, boolean clamp) {
    FPoint2 loc = panelToWorld(e.getPoint());
    loc.x = math.clamp(loc.x, wStart.x, wEnd.x);
    loc.y = math.clamp(loc.y, wStart.y, wEnd.y);

    return loc;
  }

  public FPoint2 getMousePtF(MouseEvent e) {
    return getMousePtF(e, false);
  }

  public int getWidth() {
    return getSize().width;
  }

  public int getHeight() {
    return getSize().height;
  }

  /**
   * Set the world -> panel transformation to fit a particular
   * width and height.  Performs letterboxing if aspect ratios
   * differ.
   * @param w width to fit; negate to flip in x
   * @param h height to fit; negate to flip in y
   * @param margin extra margin pixels around edges if desired
   */
  public void setView(double w, double h, int margin) {
    setView(0, 0, w, h, margin);
  }

  public void setView(double x1, double y1, double x2, double y2, double aspect,
                      int margin) {
    double w = x2 - x1, h = y2 - y1;
    this.aspect = aspect;
    flipX = (w < 0);
    flipY = (h < 0);
    scaleToFitW = Math.abs(w);
    scaleToFitH = Math.abs(h);
    fitMargin = margin;
    originX = flipX ? x2 : x1;
    originY = flipY ? y2 : y1;
    calculateTransform();
  }

  /**
   * Set world -> panel transformation to fit a particular bounds.
   * Performs letterboxing if aspect ratios
   * differ.
   * @param x1 minimum xcoordinate
   * @param y1 minimum ycoordinate
   * @param x2 maximum xcoordinate; if < x1, image will be flipped
   * @param y2 maximum ycoordinate; if < y1, image will be flipped
   * @param margin extra margin pixels around edges if desired
   */
  public void setView(double x1, double y1, double x2, double y2, int margin) {
    setView(x1, y1, x2, y2, 1, margin);
  }

  public Dimension addBorders(Point pt) {
    return addBorders(new Dimension(pt.x, pt.y));
  }

  public Dimension addBorders(Dimension d) {
    Insets in = insets();
    return new Dimension(d.width + in.left + in.right,
                         d.height + in.top + in.bottom);
  }

  public Point worldToPanel(Point src) {
    Point pt = new Point();
    worldToPanel(src.x, src.y, pt);
    return pt;
  }

  private static final FPoint2 fwork = new FPoint2();
  public void worldToPanel(double x, double y, Point dest) {

    Draw2D.apply(toView, x, y, fwork);
    dest.setLocation(fwork.point());
  }

  public FPoint2 panelToWorld(Point src) {
    FPoint2 dest = new FPoint2();
    Draw2D.apply(toWorld, src.x
                 - insetsInterior.left
                 , src.y
                 - insetsInterior.top
                 , dest);
    return dest;
  }

  public Point panelToWorldI(Point src) {
    return panelToWorld(src).point();
  }

  // --- MouseListener interface---------------
  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  // --- end of MouseListener interface--------

  // --- MouseMotionListener interface---------

  public void mouseMoved(MouseEvent e) {
  }

  public void mouseDragged(MouseEvent e) {
  }

  // ------- end of interface -----------------

  private Graphics getOffscreenBuffer() {
    if (needNewOffscreenBuffer()) {
      contentsValid = false;
      offscreenSize = new Dimension(getSize().width, getSize().height);
      offscreenImage = createImage(offscreenSize.width, offscreenSize.height);
    }
    return offscreenImage.getGraphics();
  }

  private int[] xp = null;
  private int[] yp = null;
  public void fillPolygon(Point[] polyPts) {
    if (xp == null || xp.length < polyPts.length) {

      xp = new int[polyPts.length];
      yp = new int[polyPts.length];
    }
    final Point pt = new Point();
    for (int i = 0; i < polyPts.length; i++) {
      worldToPanel(polyPts[i].x, polyPts[i].y, pt);
      xp[i] = pt.x;
      yp[i] = pt.y;
    }
    graf.fillPolygon(xp, yp, polyPts.length);
  }

  public void drawLine(FPoint2 pt1, FPoint2 pt2) {
    drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
  }

  public void drawRect(FPoint2 pt1, double r) {
    drawRect(pt1.x - r, pt1.y - r, r * 2, r * 2);
  }

  public void drawLine(Point pt1, Point pt2, int hlType) {
    final int[] hl0 = {
      0, 0}
      , hl1 = {
      0, 0, -1, 0, 1, 0, 0, -1, 0, 1}
      , hls[] = {
      hl0, hl1
    };
    int[] s = hls[hlType];
    final Point p1 = new Point(), p2 = new Point();

    worldToPanel(pt1.x, pt1.y, p1);
    worldToPanel(pt2.x, pt2.y, p2);

    for (int i = 0; i < s.length; i += 2) {
      drawTfmLine(p1.x + s[i], p1.y + s[i + 1], p2.x + s[i], p2.y + s[i + 1]);
//      graf.drawLine(p1.x + s[i], p1.y + s[i + 1], p2.x + s[i], p2.y + s[i + 1]);
    }
  }

  /**
   * Determine clip flags for point
   * @param x : xcoord
   * @param y : ycoord
   * @return bit flags, each flag set if
   *  out of view to left, right, top, bottom
   */
  private static int cf(double x, double y) {
    int f = 0;
    if (x < clipR.x) {
      f |= 1;
    }
    if (x > clipR.x + clipR.width) {
      f |= 2;
    }
    if (y < clipR.y) {
      f |= 4;
    }
    if (y > clipR.y + clipR.height) {
      f |= 8;
    }
    return f;
  }

  /**
   * Clip a line to the clip rectangle, which is in clipR
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @return true if line not completely clipped out, with
   *   endpoints in c[0..3]
   */
  private static boolean clipLineToRect(double x1, double y1, double x2,
                                        double y2) {

    final boolean d = false;
    if (d) {
      System.out.println("clipLineToRect " + x1 + " " + y1 + " " + x2 + " " +
                         y2);
    }
    int f1 = cf(x1, y1),
      f2 = cf(x2, y2);
    if (d) {
      System.out.println(" f1=" + f1 + " f2=" + f2);
    }
    int cnt = 0;
    while ( (f1 | f2) != 0) {
      if (d) {
        tools.ASSERT(++cnt < 8);
      }
      if ( (f1 & f2) != 0) {
        return false;
      }
      int i = 0;
      int f = 0;
      for (; ; i++) {
        f = 1 << i;
        if ( ( (f1 | f2) & f) != 0) {
          break;
        }
      }
      if (d) {
        System.out.println(" clipping to side " + i);
      }
      if ( (f1 & f) != 0) {
        if (d) {
          System.out.println("  swapping coords");
        }
        double x = x1, y = y1;
        x1 = x2;
        y1 = y2;
        x2 = x;
        y2 = y;
        int ft = f2;
        f2 = f1;
        f1 = ft;
      }

      double n = 0;
      double m = 0;
      switch (i) {
        case 0:
          y2 = (clipR.x - x1) / (x2 - x1) * (y2 - y1) + y1;
          x2 = clipR.x;
          f2 = cf(x2, y2);
          break;
        case 1:
          m = clipR.x + clipR.width;
          y2 = (m - x1) / (x2 - x1) * (y2 - y1) + y1;
          x2 = m;
          f2 = cf(x2, y2);
          break;
        case 2:
          x2 = (clipR.y - y1) / (y2 - y1) * (x2 - x1) + x1;
          y2 = clipR.y;
          f2 = cf(x2, y2);
          break;
        case 3:
          m = clipR.y + clipR.height;
          x2 = (m - y1) / (y2 - y1) * (x2 - x1) + x1;
          y2 = m;
          f2 = cf(x2, y2);
          break;
      }
      if (d) {
        System.out.println("  new x2=" + x2 + " y2=" + y2 + "  flags=" + f2);
      }
    }
    c[0] = x1;
    c[1] = y1;
    c[2] = x2;
    c[3] = y2;
    return true;
  }

  private static final double[] c = new double[4];
  private static Rectangle clipR;

  public void drawLine(Point p1, Point p2) {
    drawLine(p1.x, p1.y, p2.x, p2.y);
  }

  public void drawVectorScript(double[] script, FPoint2 origin, double scale) {
    double x0 = 0, y0 = 0;
    boolean first = true;
    double baseScale = scale;

    for (int i = 0; i < script.length; ) {

      double cmd = script[i];
      if (cmd == VJ) {
        first = true;
        i++;
      }
      else if (cmd == VC) {
        int ci = (int) script[i + 1];
        int level = (int) script[i + 2];
        i += 3;
        setColor(Colors.get(ci, level));

      }
      else if (cmd == VSCL) {
        baseScale = script[i + 1] * scale;
        i += 2;
      }
      else {
        double x = origin.x + script[i] * baseScale;
        double y = origin.y + script[i + 1] * baseScale;
        if (!first) {
          drawLine(x0, y0, x, y);
        }
        x0 = x;
        y0 = y;
        first = false;
        i += 2;
      }
    }
  }

  public void drawLine(double x1, double y1, double x2, double y2) {
    final Point p1 = new Point(), p2 = new Point();
    worldToPanel(x1, y1, p1);
    worldToPanel(x2, y2, p2);
    drawTfmLine(p1.x, p1.y, p2.x, p2.y);
//    graf.drawLine(p1.x, p1.y, p2.x, p2.y);
  }

  public int viewPixels(int pix) {
    return (int) (pix * Math.abs(toWorld.vals[0]));
  }

  public void drawRect(int x, int y, double w, double h, int inset) {
    drawRect(x - inset, y - inset, w + 2 * inset, h + 2 * inset);
  }

  public void fillRect(Rectangle r) {
    fillRect(r.x, r.y, r.width, r.height);
  }

  public void fillRect(double x, double y, double w, double h) {
    final Point p1 = new Point(), p2 = new Point();
    worldToPanel(x, y, p1);
    worldToPanel(x + w, y + h, p2);
    graf.fillRect(p1.x, p2.y, p2.x - p1.x, p1.y - p2.y);
  }

  public void drawRect(double x, double y, double w, double h) {
    final Point p1 = new Point(), p2 = new Point();
    worldToPanel(x, y, p1);
    worldToPanel(x + w, y + h, p2);
    graf.drawRect(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
                  Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));

  }

  /*
    public void drawRect(int x, int y, int w, int h) {
       final Point p1 = new Point(), p2 = new Point();
      worldToPanel(x, y, p1);
      worldToPanel(x + w, y + h, p2);
      graf.drawRect(p1.x, p2.y, p2.x - p1.x, p1.y - p2.y);
    }
   */
  private boolean needNewOffscreenBuffer() {
    return (
      offscreenImage == null
      || getSize().width != offscreenSize.width
      || getSize().height != offscreenSize.height
      );
  }

  /**
   * Helper method for setting GridBag constraints
   * @param gbc GridBagConstraints object to modify
   * @param gx gridx value
   * @param gy gridy value
   * @param gw gridwidth value
   * @param gh gridheight value
   * @param wx weightx value
   * @param wy weighty value
   */
  public static GridBagConstraints setGBC(GridBagConstraints gbc, int gx,
                                          int gy,
                                          int gw, int gh, int wx, int wy) {
    gbc.gridx = gx;
    gbc.gridy = gy;
    gbc.gridwidth = gw;
    gbc.gridheight = gh;
    gbc.weightx = wx;
    gbc.weighty = wy;
    return gbc;
  }

  private void calculateInteriorSize() {
    iPaintWidth = getSize().width;
    iPaintHeight = getSize().height;

    interiorWidth = iPaintWidth - insetsInterior.left - insetsInterior.right;
    interiorHeight = iPaintHeight - insetsInterior.top - insetsInterior.bottom;

    rInterior = new Rectangle(
      insetsInterior.left, insetsInterior.top,
      interiorWidth, interiorHeight);
  }

  // Determine if any of the frame needs repainting.
  // Returns false if the update region is confined to the interior.
  private boolean frameDirty(Graphics g) {
    return (!math.rectContainsRect(rInterior, rDirty));
  }

  private void paintFrame(Graphics g) {

    // Fill the border with the bgnd color
    g.setColor(colorBgnd);
    g.fillRect(0, 0, iPaintWidth, insetsInterior.top);
    g.fillRect(0, iPaintHeight - insetsInterior.bottom, iPaintWidth,
               insetsInterior.bottom);
    g.fillRect(0, insetsInterior.top, insetsInterior.left, rInterior.height);
    g.fillRect(iPaintWidth - insetsInterior.right, insetsInterior.top,
               insetsInterior.right, rInterior.height);

    if (iTitleHeight != 0) {
      g.setColor(colorTitle);
      g.fillRect(iOutsideMarginSize, iOutsideMarginSize,
                 iPaintWidth - iOutsideMarginSize * 2, iTitleHeight);

      Font save = g.getFont();
      g.setFont(titleFont);
      FontMetrics fm = g.getFontMetrics(titleFont);
      g.setColor(Color.black);
      int tx = (iPaintWidth - fm.stringWidth(sTitle)) / 2;
      int ty = iOutsideMarginSize + TITLE_HEIGHT / 2 + fm.getAscent() / 2 -
        fm.getDescent();
      g.drawString(sTitle, tx, ty);

      g.setFont(save);
    }

    drawBevelEdge(g, iOutsideMarginSize, 0, 0, iPaintWidth, iPaintHeight, false);

    switch (iStyle) {
      case STYLE_SUNKEN: {
        // Draw a sunken border one pixel wide
        int iEtchWidth = iPaintWidth - insetsEtch.left - insetsEtch.right;
        int iEtchHeight = iPaintHeight - insetsEtch.top - insetsEtch.bottom;

        drawBevelEdge(g, 1,
                      insetsEtch.left, insetsEtch.top,
                      insetsEtch.left + iEtchWidth,
                      insetsEtch.top + iEtchHeight,
                      true);
      }
      break;

      case STYLE_ETCH: {
        // Draw an etched border a few pixels inside

        int iEtchWidth = iPaintWidth - (1 + insetsEtch.left + insetsEtch.right);
        int iEtchHeight = iPaintHeight -
          (1 + insetsEtch.top + insetsEtch.bottom);

        g.setColor(colorLighter);
        g.drawRect(insetsEtch.left + 1, insetsEtch.top + 1, iEtchWidth,
                   iEtchHeight);
        g.setColor(colorDarker);
        g.drawRect(insetsEtch.left, insetsEtch.top, iEtchWidth, iEtchHeight);
      }
      break;
    }
  }

  private final Point work = new Point();
  public void fillOval(double x, double y, double size) {
    worldToPanel(x, y, work);
    graf.fillOval( (int) (work.x - size / 2), (int) (work.y - size / 2),
                  (int) size, (int) size);
  }

  public void drawOval(FPoint2 pt, double size) {
    drawOval(pt.x, pt.y, size);
  }

  public void fillOval(FPoint2 pt, double size) {
    fillOval(pt.x, pt.y, size);
  }

  public void drawOval(int x, int y, int size) {
    worldToPanel(x, y, work);
    graf.drawOval(work.x - size / 2, work.y - size / 2, size, size);
  }

  public void drawOval(double x, double y, double sz) {
    worldToPanel(x, y, work);
    int size = (int) sz;
    graf.drawOval(work.x - size / 2, work.y - size / 2, size, size);
  }

  public void drawString(String s, double x, double y) {
    worldToPanel(x, y, work);
    drawTfmString(s, work.x, work.y);
  }

  /**
   * Draw a string at a point, with optional solid background
   * @param s : string to plot
   * @param pt : location to plot string
   * @param bgColor : if not null, clears background to this color
   */
  public void drawString(String s, FPoint2 pt, Color bgColor,
                         double dwidth, double dheight) {

    if (bgColor != null) {

      double width = (s.length() * dwidth);
      double height = dheight;
      Color sc = this.getColor();
      setColor(bgColor);
      fillRect(pt.x, pt.y, width, height);
      /*
        (pt.x - width / 2),
        (pt.y - height / 2),
        width, height);
       */
      setColor(sc);
    }
    worldToPanel(pt.x, pt.y, work);
    drawTfmString(s, work.x, work.y);

  }

  public void drawString(String s, FPoint2 pt) {
    drawString(s, pt, null, 0, 0);
    /*    worldToPanel(pt.x,pt.y,work);
        graf.drawString(s,work.x,work.y);
     */
  }

  private static Matrix matrix() {
    Matrix m = new Matrix(3);
    m.vals[0] = m.vals[1] = m.vals[2] = 1.0;
    return m;
  }

  private void restoreOrigin(Graphics g) {
    g.translate( -rInterior.x, -rInterior.y);
  }

  /** Move the origin and adjust the clipping rectangle to fit the interior
   * of the panel.  The origin is moved so (0,0) is in the top left of the
   * interior of the panel.
   * Returns rectangle containing portion needing updating.
   */
  private Rectangle prepareInterior(Graphics g) {
    Rectangle r = rDirty.intersection(rInterior);
    g.translate(rInterior.x, rInterior.y);
    if (!r.isEmpty()) {
      r.translate( -rInterior.x, -rInterior.y);
      g.setClip(r.x, r.y, r.width, r.height);
    }
    return r;
  }

  public static Font utilFont() {
    if (utilFont == null) {
      utilFont = new Font("Sanserif", Font.PLAIN, 10);
    }
    return utilFont;
  }

  protected void init(String title, int iStyle, Color c) {

    addComponentListener(this);

    if (title != null) {
      sTitle = title;
      iTitleHeight = TITLE_HEIGHT;
      if (false) {
        iTitleHeight *= 5;
        System.out.println("making titleHeight larger");
      }
      if (titleFont == null) {
        titleFont = new Font(
          "Sanserif", Font.PLAIN, (iTitleHeight * 110) / 128
          );
      }
    }
    fPaintPrepared = false;

    switch (iStyle) {
      case STYLE_SUNKEN:
        iOutsideMarginSize = 3;
        insetsEtch = new Insets(6 + iTitleHeight, 6, 6, 6);
        insetsInterior = new Insets(7 + iTitleHeight, 7, 7, 7);
        break;

      case STYLE_ETCH:
        iOutsideMarginSize = 2;
        insetsEtch = new Insets(5 + iTitleHeight, 5, 5, 5);
        insetsInterior = new Insets(10 + iTitleHeight, 10, 10, 10);
        break;

      default:

        // In case the caller specified an unsupported style,
        // change style to PLAIN.
        iStyle = STYLE_PLAIN;
        iOutsideMarginSize = 2;
        insetsInterior = new Insets(5 + iTitleHeight, 5, 5, 5);
        break;
    }

    this.iStyle = iStyle;
    if (c == null) {
      c = defaultColor;
    }

    frameColor(c);

    setView(1.0, 1.0, 0);
    calculateInteriorSize();
  }

  private static Color colorAdjust(Color c, int iFactor) {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();

    r = math.clamp( (r * iFactor) >> 8, 0, 255);
    g = math.clamp( (g * iFactor) >> 8, 0, 255);
    b = math.clamp( (b * iFactor) >> 8, 0, 255);

    return new Color(r, g, b);
  }

  private void frameColor(Color c) {

    colorBgnd = colorAdjust(c, 0x100);
    colorTitle = colorAdjust(c, 0x120);
    colorLighter = colorAdjust(c, 0x140);
    colorDarker = colorAdjust(c, 0xC0);

    switch (iStyle) {
      case STYLE_SUNKEN:
        setBackground(colorDarker);
        break;
      default:
        setBackground(colorBgnd);
        break;
    }
  }

// ----------------- paint stuff

  /**
   * Overriden update() function, to avoid flickering.  No erasing
   * occurs.
   */
  public void update(Graphics g) {
    paint(g);
  }

  /**
   * Paint the panel.  The frame is drawn if necessary.  To paint
   * the interior of the panel, the paintInterior() method is called.
   */
  public void paint(Graphics graphics) {
    graf = getOffscreenBuffer();

    // Paint into the offscreen buffer.

    // Use the same clipping rectangle as the input context,
    // unless the offscreen buffer is invalid, in which case
    // we use the entire size.

    Rectangle clip = graphics.getClipBounds();
    if (!contentsValid) {
      clip = getBounds();
      clip.x = 0;
      clip.y = 0;
    }
    graf.setClip(clip);

    beginPaint(graf);
    if (rDirty != null) {
      if (frameDirty(graf)) {
        paintFrame(graf);

      }
      Rectangle rUpdate = prepareInterior(graf);
      rInt2 = new Rectangle(rInterior);

      if (!rUpdate.isEmpty()) {

        paintInterior(graf);
// ------------------
        if (false) {
          graf.setColor(debugAnim ? Color.blue : Color.red);
          debugAnim ^= true;
          Rectangle r = graf.getClipBounds();
          int inset = debugAnim ? 5 : 0;
          graf.drawOval(r.x + inset, r.y + inset, r.width - 2 * inset,
                        r.height - 2 * inset);
        }
//
      }
      restoreOrigin(graf);
    }
    endPaint();

    graf.dispose();
    graf = null;

    contentsValid = true;

    graphics.drawImage(offscreenImage, 0, 0, this);
  }

  private boolean debugAnim;

  /**
   * Paint the dirty part of the panel's interior
   *  g = Graphics object
   *  fBufferValid = true if panel is double buffered and existing contents
   *    may be valid.  If true, calling program can assume no painting is
   *    required unless their own data has changed.  If false, entire
   *    rUpdate requires repaint.
   *
   *  The origin has been adjusted so (0,0) is the top left of the interior
   *  of the panel.
   *
   * The clip region of the panel has been set to the bounding
   * rectangle of the dirty part of the panel's interior.
   *
   * Override this function to perform your own object's updating.
   * This default one fills the interior with colorInterior.
   *
   */
  public void paintInterior(Graphics g) {
    g.setColor(colorBgnd);
    Rectangle r = g.getClipBounds();
    g.fillRect(r.x, r.y, r.width, r.height);
  }

  private void beginPaint(Graphics g) {
    calculateInteriorSize();
    rDirty = g.getClipBounds();
    if (rDirty != null && rDirty.isEmpty()) {
      rDirty = null;
    }
    fPaintPrepared = true;
  }

  private void endPaint() {
    rDirty = null;
    rInterior = null;
    fPaintPrepared = false;
  }

  public Graphics graphics() {
    // tools.ASSERT(graf != null);
    return graf;
  }

  public void componentHidden(ComponentEvent e) {}

  public void componentShown(ComponentEvent e) {}

  public void componentMoved(ComponentEvent e) {}

  public void componentResized(ComponentEvent e) {
    calculateTransform();
  }

  public Dimension getPreferredSize() {
//    System.out.println("getPreferredSize for "+this);
    return new Dimension(50, 50);
  }

  public void translate(double x, double y) {
    Matrix mTransI = Draw2D.translationMatrix(x, y);
    Draw2D.mult(toView, mTransI, toView);
  }

  public void pushMatrix() {
    Matrix save = new Matrix(3);
    Matrix.copy(toView, save);
    matrixStack.push(save);
  }

  public void popMatrix() {
    toView = (Matrix) matrixStack.pop();
  }

  private void calculateTransform() {

    calculateInteriorSize();
    toView = new Matrix(3);
    toWorld = new Matrix(3);

    double tx = 0, ty = 0;
    double sx = 1.0, sy = 1.0;

    {
      Point pt = getInteriorSize();
      int mx = pt.x - 2 * fitMargin;
      int my = pt.y - 2 * fitMargin;

      double xs = mx / (scaleToFitW * aspect);
      double ys = my / scaleToFitH;

      double scale = Math.min(xs, ys);
      sx = scale * aspect * (flipX ? -1 : 1);
      sy = scale * (flipY ? -1 : 1);
      scaleX = 1.0 / (scale * aspect);
      scaleY = 1.0 / (scale);
      // Calculate the letterbox values.
      double wUsed = Math.abs(sx * scaleToFitW);
      double hUsed = Math.abs(sy * scaleToFitH);

      tx = (mx - wUsed) / 2;
      ty = (my - hUsed) / 2;
      if (flipX) {
        tx = mx - tx;
      }
      if (flipY) {
        ty = my - ty;
      }
      tx += fitMargin;
      ty += fitMargin;
    }
    scaleFactor = sx;
    scaleFactorR = 1 / scaleFactor;

    Matrix mScale = Draw2D.scaleMatrix(sx, sy);
    Matrix mTrans = Draw2D.translationMatrix(tx, ty);
    Matrix mTrans2 = Draw2D.translationMatrix( -originX, -originY);

    toView = Draw2D.translationMatrix(0, 0);

    Draw2D.mult(toView, mTrans, toView);
    Draw2D.mult(toView, mScale, toView);
    Draw2D.mult(toView, mTrans2, toView);

    // Calculate reverse matrix.
    Matrix mScaleI = Draw2D.scaleMatrix(1 / sx, 1 / sy);
    Matrix mTransI = Draw2D.translationMatrix( -tx, -ty);
    Matrix mTransI2 = Draw2D.translationMatrix(originX, originY);

    Draw2D.mult(mTransI2, mScaleI, mScaleI);
    Draw2D.mult(mScaleI, mTransI, toWorld);
//    System.out.println("toView=");toView.display();
//    System.out.println("toWorld=");toWorld.display();

    // Construct points defining the bottom left and top right
    // of the viewable world coordinates.

    {
      FPoint2 w0 = panelToWorld(new Point(rInterior.x, rInterior.y));
      FPoint2 w1 = panelToWorld(new Point(rInterior.x + rInterior.width,
                                          rInterior.y + rInterior.height));
      wStart = new FPoint2(Math.min(w0.x, w1.x), Math.min(w0.y, w1.y));
      wEnd = new FPoint2(Math.max(w0.x, w1.x), Math.max(w0.y, w1.y));
    }
  }

  /**
   * Return a point representing either the minimum or maximum world
   * coordinates visible.
   * @param max : if true, returns maximum
   * @return point
   */
  public FPoint2 getVisibleWorld(boolean max) {
    return new FPoint2(max ? wEnd : wStart);
  }

  public FPoint2 clampToVisible(FPoint2 pt, double inset) {
    FPoint2 p2 = new FPoint2(pt);
    p2.x = math.clamp(p2.x, wStart.x + inset, wEnd.x - inset);
    p2.y = math.clamp(p2.y, wStart.y + inset, wEnd.y - inset);
    return p2;
  }

  public double scaleFactor() {
    return scaleFactor;
  }

  public double pixels(double worldDist) {
    return (worldDist * scaleFactorR);
  }


  public double scaleX, scaleY;
  private double scaleFactor;
  private double scaleFactorR;
  public static final Color defaultColor = new Color(240, 240, 240);
  public Matrix toView, toWorld;
  private double aspect;

  // double buffering:
  private Image offscreenImage;
  private Dimension offscreenSize;
  private boolean contentsValid;

  private int iStyle;

  private int iOutsideMarginSize; // # pixels of bevelling on the outside borders
  private Insets insetsEtch; // insets for etched border
  private Insets insetsInterior; // insets for interior

  private Color colorBgnd;
  private Color colorLighter;
  private Color colorDarker;
  private Color colorTitle;

  // These are used within beginPaint()...endPaint() calls:

  private boolean fPaintPrepared;
  private int iPaintWidth;
  private int iPaintHeight;
  private Rectangle rInterior;
  private Rectangle rDirty;
  private Rectangle rInt2;

  // double buffering:
  private boolean fContentsValid;

  // For keeping track of whether repainting occurred, for
  // triggering the event dispatch thread:
  private static int iRepaintX, iRepaintY;
  private static boolean fRepainted;
  private int interiorWidth, interiorHeight;

  private static final int TITLE_HEIGHT = 16;
  private String sTitle = null;
  private int iTitleHeight = 0;
  private Font titleFont;
  private static Font utilFont;

  private double scaleToFitW, scaleToFitH;
  private boolean flipX, flipY;
  private int fitMargin;

  private FPoint2 wStart, wEnd;
  private Graphics graf;
  private DStack matrixStack = new DStack();
  private double originX, originY;

// --------- postScript support
  private boolean psMode() {
    return (graf_ps != null);
  }

  private void drawTfmLine(double x1, double y1, double x2, double y2) {
    if (psMode()) {
      drawTfmLine_ps(x1,y1,x2,y2);

    }
    else {
      graf.drawLine( (int) x1, (int) y1, (int) x2, (int) y2);
    }
  }

  private static Font tfmFont;
  private void drawTfmString(String s, double x, double y) {
    if (psMode()) {
drawTfmString_ps(s,x,y);


    }
    else {
      graf.drawString(s, (int) x, (int) y);
    }
  }

// Exchange these lines for the ones below for postscript support
// (not possible in browser)
// /*
private Graphics graf_ps;
private void drawTfmString_ps(String s, double x, double y) {}
private void drawTfmLine_ps(double x1, double y1, double x2, double y2) {}
public void postScript(String path) {}
// */
/*
  private Graphics2D graf_ps;
  private void drawTfmString_ps(String s, double x, double y) {
        if (tfmFont == null) {
          tfmFont = new Font("serif", Font.PLAIN, 12);
        }
        graf_ps.setFont(tfmFont);
        graf_ps.drawString(s, (float) x, (float) y);
  }
  private void drawTfmLine_ps(double x1, double y1, double x2, double y2) {
  graf_ps.setPaint(getColor());
  Rectangle r = graf.getClipBounds();
  clipR = r;
  if (! (r.contains(x1, y1) && r.contains(x2, y2))) {
    if (!clipLineToRect(x1, y1, x2, y2)) {
      return;
    }
    x1 = c[0];
    y1 = c[1];
    x2 = c[2];
    y2 = c[3];
  }
  java.awt.geom.Line2D ln = new java.awt.geom.Line2D.Double(x1, y1, x2, y2);
  graf_ps.setPaint(prevColor);
  graf_ps.draw(ln);
  }
  // Generate a postscript file for the panel
  public void postScript(String path) {
    // if it's never been drawn on screen, we don't know
    // its clipping bounds; do nothing.
    if (rInt2 == null) {
      System.out.println("panel must be drawn on screen first");
      return;
    }

    org.jibble.epsgraphics.EpsGraphics2D g2 = new org.jibble.epsgraphics.EpsGraphics2D();
//    g2.setBackground(Color.white);
    graf_ps = g2;
    graf = g2;
    graf.setClip(rInt2.x, rInt2.y, rInt2.width, rInt2.height);

    beginPaint(graf);

    Rectangle rUpdate = prepareInterior(graf);
    if (!rUpdate.isEmpty()) {
      graf_ps.setClip(rUpdate.x, rUpdate.y, rUpdate.width, rUpdate.height);
      paintInterior(graf);
    }
    restoreOrigin(graf);

    String output = graf.toString();

    if (path == null) {
      path = "out.eps";

    }
    System.out.println("writing " + path + ", size=" + rInt2.width + "," +
                       rInt2.height);
    try {
      PrintWriter w = new PrintWriter(new FileWriter(path));
      w.println(output);
      w.close();
    }
    catch (IOException e) {
      System.out.println("I/O exception writing " + path + ": " + e);
    }
    graf = null;
    graf_ps = null;
  }
// */
// -----------------------------
}