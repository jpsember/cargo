package myUtils;

import java.awt.*;

public class Face
    extends Renderable {
  public Face(int va, int vb, int vc, int f) {
    init();
    v0 = va;
    v1 = vb;
    v2 = vc;
    flags = f;
  }

  public Face() {
    init();
  }

  private void init() {
    type = RENDERABLE_FACE;
  }

  public void transform(view3D view, double[] viewVert, int[] panelVert) {

    this.panelVert = panelVert;

    // Determine z-sort value.
    {
      double z0 = viewVert[v0 * 3 + 2],
          z1 = viewVert[v1 * 3 + 2],
          z2 = viewVert[v2 * 3 + 2];

      double zMax, zMin;
      if (z0 > z1) {
        zMax = (z0 > z2 ? z0 : z2);
        zMin = (z2 > z1 ? z1 : z2);
      }
      else {
        zMax = (z1 > z2 ? z1 : z2);
        zMin = (z2 > z0 ? z0 : z2);
      }

      zSortValue = (zMax + zMin) * .5;
      if ( (flags & FF_FARSORT) != 0)
        zSortValue = zMin;
     /* if ( (flags & FF_NEARSORT) != 0) {
        zSortValue = (zMax * 3 + zMin) * .25;
       }
      */
    }

    /*
     This is probably a little slow!
     {
      // Determine lighting value from normal to face.
      int a = v0*3, b = v1*3, c = v2*3;
      double sx = viewVert[a+0] - viewVert[c+0];
      double sy = viewVert[a+1] - viewVert[c+1];
      double sz = viewVert[a+2] - viewVert[c+2];
      double tx = viewVert[b+0] - viewVert[c+0];
      double ty = viewVert[b+1] - viewVert[c+1];
      double tz = viewVert[b+2] - viewVert[c+2];
      double cx = sy * tz - sz * ty;
      double cy = sz * tx - sx * tz;
      double cz = sx * ty - sy * tx;
      double len = Math.sqrt(cx*cx+cy*cy+cz*cz);
      double level = (cx+cy+cz) / (2*len) + .8;
      this.level = (int)((level) * ((flags & FF_SHADE) >> 8));
      }
     */
    view.add(this);
  }

  // Arrays to construct polygon vertices within
  private static final int[] x = new int[3];
  private static final int[] y = new int[3];

  /**
   * Render the object
   * @param view : the current view
   */
  public void render(view3D view) {
    Graphics g = view.graphics();

    setColor(g);

    int k = v0 * 2;
    x[0] = panelVert[k + 0];
    y[0] = panelVert[k + 1];
    k = v1 * 2;
    x[1] = panelVert[k + 0];
    y[1] = panelVert[k + 1];
    k = v2 * 2;
    x[2] = panelVert[k + 0];
    y[2] = panelVert[k + 1];

    if ( (flags & FF_WIREFRAME) != 0
        || view.wireframeMode()
        )
      g.drawPolygon(x, y, 3);
    else {
      g.fillPolygon(x, y, 3);
      if ( (flags & FF_BORDER) != 0) {
        int shade = getShade();
        Colors.set(g, flags & RF_COLOR, (shade * 3) / 4);
        g.drawPolygon(x, y, 3);
      }
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Face v(" + v0 + " " + v1 + " " + v2 + ")");
    sb.append(" flags:" + flags);
    return sb.toString();
  }

  // Vertex indices (0...n-1)
  public int v0, v1, v2;
  // Array of panel-space vertex coordinates
  private int[] panelVert;

}