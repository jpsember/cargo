package myUtils;

import java.awt.*;

public class Circle3D extends Renderable {
  public int v;
  public double radius;
  private int pRadius;

  public Circle3D(int v, double radius, int flags) {
    this.v = v;
    this.radius = radius;
    this.flags = flags;
    type = RENDERABLE_CIRCLE;
  }

  private int[] panelVert;
  public void transform(view3D view, double[] viewVert, int[] panelVert) {
    this.panelVert = panelVert;
    zSortValue = viewVert[v * 3 + 2];
    pRadius = (int)(view.project(radius,zSortValue));

    view.add(this);
  }

  /**
   * Render the object
   * @param view : the current view
   */
  public void render(view3D view) {
    Graphics g = view.graphics();

    setColor(g);

    int k = v * 2;
    int diam = pRadius * 2;
    int x1 = panelVert[k + 0] - pRadius,
        y1 = panelVert[k + 1] - pRadius;

    int thickness = (flags & CF_THICK) != 0 ?
        3 : 1;

    for (int i = 0; i < thickness; i++)
      g.drawOval(x1+i,y1+i,diam - i*2, diam - i*2);
  }

}
