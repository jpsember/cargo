package myUtils;

import java.awt.*;

public class Vert3D extends Renderable
    implements MeshConst {

  public FPoint3 pt;
  private int index;

   public Vert3D(double x, double y, double z, int index, double r) {
    type = RENDERABLE_VERTEX;
    pt = new FPoint3(x,y,z);
    this.index = index;
    radius = r;
    setColor(BLUE,.5);
  }
  public void setRadius(double r) {
    radius = r;
  }
  public double getRadius() {
    return radius;
  }


  private double radius;
  private int px, py, pRadius;

  public void transform(view3D view, double[] viewVert, int[] panelVert) {
    // Determine z-sort value.
    px = panelVert[index*2+0];
    py = panelVert[index*2+1];
    if (radius != 0) {
      pRadius = (int)(view.project(radius,viewVert[index*3+2]));
      zSortValue =  viewVert[index*3+2] + radius;
      view.add(this);
    }
  }

  /**
   * Render the object
   * @param view : the current view
   */
  public void render(view3D view)
  {
    Graphics g = view.graphics();

    setColor(g);
//    int shade = getShade();
//    Colors.set(g,flags & FF_COLOR, shade);

    g.fillOval(px - pRadius,py - pRadius, pRadius*2, pRadius*2);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Vert3D "+super.toString());
    sb.append(" flags:" + flags);
    return sb.toString();
  }

}

