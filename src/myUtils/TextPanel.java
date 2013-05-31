package myUtils;
import java.awt.*;

public class TextPanel extends myPanel {
  private int rows;
  private int rowHeight;
  private String[] text;
  private int firstRow;
  private int nextRow;

  private static final int
   Y_MARGIN = 4,
   X_MARGIN = 6,
   Y_SEP = 1;

  public TextPanel(String title, int rows) {
    super(title,STYLE_ETCH,Color.white);
    init(title, rows);
  }

  private String prevPrompt;
  public void setPrompt(String s) {
    if (s != prevPrompt) {
      prevPrompt = s;
      setText(0,s);
      repaint(200);
    }
  }

  private void init(String title, int nRows) {
    if (title != null)
      nRows++;
    rows = nRows;
    setBackground(Color.lightGray);
    rowHeight = 14 + Y_SEP;
    text = new String[rows];
    firstRow = 0;
    if (title != null) {
      text[0] = title;
      firstRow++;
    }
    nextRow = firstRow;
    this.resize(getPreferredSize());
  }

  public void setText(int row, String s) {
    nextRow = row;
    setText(s);
  }

  public void setText(String s) {
    text[firstRow + nextRow++] = s;
  }

  public void clearToEnd() {
    while (nextRow < rows - firstRow)
      setText(null);
  }

  public Dimension getMinimumSize() {
    int width = 640;
    int height = 80;
    Insets i = insets();
    Component p = getParent();
    if (p != null) {
      width = p.getSize().width;
    }
    height =     rows * rowHeight - Y_SEP + i.top + i.bottom;
    Dimension d = new Dimension(width, height);
    return d;
  }

public Dimension getPreferredSize() {
    return getMinimumSize();
}

  private static Font font;

  private static void initFont(Graphics g) {
    if (font == null) {
      font = new Font("Monospaced",Font.PLAIN,12);
    }
  }

  public void paintInterior(Graphics g) {
    super.paintInterior(g);
    initFont(g);
    g.setFont(font);
    FontMetrics fm = g.getFontMetrics();

    int rowBase =  fm.getAscent()/2;
    g.setColor(Color.black);
    for (int r = 0; r < rows; r++) {
      String s = text[r];
      if (s == null) continue;

      int y = rowHeight * r + rowBase + Y_MARGIN - Y_SEP;
      int x = X_MARGIN;

      boolean centered = false;
      if (s.startsWith(".c ")) {
        centered = true;
        s = s.substring(3);
        x = (getWidth() - fm.stringWidth(s))/2;
      }
      g.drawString(s, x, y);
    }
  }
}