package myUtils;

import java.awt.*;

public class Line3D extends Renderable {
  public double x1, y1, z1,
      x2, y2, z2;
  public int v0, v1;

public Object user; // DEBUG only!!!!
/*
  public void setHighlight(boolean hl) {
    if (hl) {
      setColor(RED,40);
    } else {
      setColor(BLUE,40);
    }
  }
*/
public String toString() {
  return super.toString()+" Line3D v0="+v0+", v1="+v1;
}
  public Line3D(int v0, int v1, int flags) {
    this.v0 = v0;
    this.v1 = v1;
    this.flags = flags;
    type = RENDERABLE_LINE;
  }

  private int[] panelVert;
  public void transform(view3D view, double[] viewVert, int[] panelVert) {
    this.panelVert = panelVert;

    // Determine z-sort value.

    double z0 = viewVert[v0 * 3 + 2],
        z1 = viewVert[v1 * 3 + 2];

    zSortValue = (z0 + z1) * .5;
    view.add(this);
  }

  /**
   * Render the object
   * @param view : the current view
   */
  public void render(view3D view) {
    Graphics g = view.graphics();
    setColor(g);

//    int shade = getShade();
//    Colors.set(g, flags & VF_COLOR, shade);

    int k = v0 * 2;
    int x1 = panelVert[k + 0];
    int y1 = panelVert[k + 1];
    k = v1 * 2;
    int x2 = panelVert[k + 0];
    int y2 = panelVert[k + 1];
    if ((flags & LF_THICK) != 0) {
      g.drawLine(x1-1,y1,x2-1,y2);
      g.drawLine(x1+1,y1,x2+1,y2);
      g.drawLine(x1,y1-1,x2,y2-1);
      g.drawLine(x1,y1+1,x2,y2+1);
      g.drawLine(x1, y1, x2, y2);
    } else {
      g.drawLine(x1, y1, x2, y2);
    }
  }


}