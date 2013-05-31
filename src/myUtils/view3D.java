package myUtils;

import java.awt.*;
import java.awt.event.*;

public class view3D
    implements
    Comparable,
    MeshConst,
    Globals {

  private boolean wireframeMode;
  public boolean wireframeMode() {
    return wireframeMode;
  }
  public void setWireframeMode(boolean s) {
    wireframeMode = s;
  }

  public void setScaleValue(double s) {
    scaleValue = s;
  }
  public view3D(myPanel panel) {
    if (Colors.maxColors() == 0) {
      Colors.init();
    }

    lightDir.set(-1.0,-1.0,1.0);
    lightDir.normalize();
    this.panel = panel;
    camera = new camera3D(this);
    setProjection(math.degrees(30), 1.0, .6);
  }

  private DStack matrixStack = new DStack();

  public void pushMatrix() {
    Matrix save = new Matrix(4);
    Matrix.copy(worldToView,save);
    matrixStack.push(save);
  }
  public Graphics graphics() {
    return graf;
  }
  public Matrix matrix() {
    return worldToView;
  }

  public void popMatrix() {
    worldToView = (Matrix)matrixStack.pop();
  }
  public void multMatrix(Matrix m) {
    Draw3D.mult(worldToView,m,worldToView);
  }

  private FPoint3 lightDir = new FPoint3();
  public FPoint3 lightDir() {
    return lightDir;
  }

  /**
   * Set up the projection.
   * @param fov angle of view; the angle the frustum makes with the eye
   * @param width width of front of frustum
   * @param height height of front of frustum
   */
  public void setProjection(double fov, double width, double height) {
    this.fov = fov;

    final double NEAR_Z_SCALE = 8.0;

    frustumWidth = width;
    this.fWidth = width / NEAR_Z_SCALE;
    this.fHeight = height / NEAR_Z_SCALE;

    // Calculate z-distance from camera to near projection plane.

    distToPlane = height / Math.tan(fov);
    nearZ = -distToPlane / NEAR_Z_SCALE;
    setScaleValue(.007 * frustumWidth);
    camera.reset();
  }

  /**
   * Determine width of view.
   * @return width of view, in world (or view) coordinates.  This is the
   * distance between the left and right sides of the view at the
   * camera focus distance.
   */
public double width() {
    return frustumWidth;
}

private double frustumWidth;

private  double distToPlane;
  public double distanceToProjectionPlane() {
    return distToPlane;

  }

  private void calcViewToPanel() {
    // Examine dimensions of panel.
    Point panelSize = panel.getInteriorSize();

    double sx = panelSize.x / fWidth;
    double sy = panelSize.y / fHeight;
    panelScale = Math.min(sx, sy);
    panelScale2 = nearZ * panelScale;

    panelOrigin.x = panelSize.x / 2;
    panelOrigin.y = panelSize.y / 2;
    /*
    System.out.println("PanelSize = "+Fmt.f(panelSize));
    System.out.println(" fWidth,Height="+Fmt.f(fWidth)+","+Fmt.f(fHeight));
    System.out.println(" scale="+Fmt.f(panelScale));
*/
  }

  public camera3D camera() {
    return camera;
  }

  public void addMouseCameraControl() {
    camera.addMouseControl(panel);
  }

  private Graphics graf;

  public void renderBegin(Graphics g) {
 //   tools.ASSERT(graf == null);
    graf = g;
    renderList.clear();

    camera.look();
    calcViewToPanel();

    // Calculate world -> view transformation matrix.
    calcWorldToView();


//    graf.setColor(Color.red);
//    graf.drawRect(panelOrigin.x - 2, panelOrigin.y - 2, 2 * 2, 2 * 2);
//    testTrans2(graf);
  }


  public void renderEnd() {

    renderList.sort(this);
    for (int i = 0; i < renderList.size(); i++) {
      Renderable r = (Renderable)renderList.get(i);
      r.render(this);
    }
    graf = null;
  }

  private DArray testMeshes = new DArray();
  public void transformTestMesh(int mesh) {

    if (mesh >= testMeshes.size())
      testMeshes.add(mesh,null);
    if (testMeshes.get(mesh) == null) {
      Mesh m = MeshUtils.testMesh(mesh, this);
      m.scale(width());

        testMeshes.set(mesh,m);
    }
    Mesh m = (Mesh)testMeshes.get(mesh);
    m.plot(this);
  }

  private void testTrans(Graphics g) {
    g.setColor(Color.blue);
    final int[] scr = {
        1, 5, 3, 5, 3, 3, 6, 3, 6, 7, 5, 7, 5, 8, 9, 8,
        9, 7, 8, 7, 8, 3, 7, 1, 2, 1, 1, 3, 1, 5

        ,0,2,10,2,10,8,0,8,0,2,
    };
    FPoint3 v0 = null;
    for (int i = 0; i < scr.length; i += 2) {
      double scl = 640 / 10.0;
      FPoint3 pt = new FPoint3((scr[i] - 5) * scl,
                               (scr[i + 1] - 5) * scl, 0);
      FPoint3 v = new FPoint3();
      Draw3D.apply(worldToView, pt, v);
      if (v.z >= nearZ
          && false
          ) {
        v0 = null;
      }
      else {
        if (v0 != null) {
          final Point2 p1 = new Point2(), p2 = new Point2();
          viewToPanel(v, p1);
          viewToPanel(v0, p2);
          g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        v0 = v;
      }
    }
  }

  public double project(double length, double z) {
    return (panelScale2 / z) * length;
  }

  public void viewToPanel(FPoint3 v, Point2 p) {
    double scale = panelScale2 / v.z; //(nearZ * panelScale) / v.z;
    p.x = ( (int) (v.x * scale)) + panelOrigin.x;
    p.y = (panelOrigin.y - (int) (v.y * scale));

  }

  private void calcWorldToView() {
    worldToView = camera.matrix();
  }

  public void repaint() {
    panel.repaint();
  }

  public void add(Renderable r) {
    renderList.add(r);
  }

  private DArray renderList = new DArray();

  // --- View to panel transformation --------
  // Center of projection, in panel pixels
  private Point2 panelOrigin = new Point2();
  // Scaling factor to convert view pixels to panel pixels
  private double panelScale, panelScale2;
  // -----------------------------------------
  // Scale value affecting speed of camera movement
  private double scaleValue;
  public double scaleValue() {
    return scaleValue;
  }

  private Matrix worldToView;

  private myPanel panel;
  private camera3D camera;
  private double fov;
  private double fWidth, fHeight;

  // Distance from camera to front of frustum
  private double nearZ;

    /**
     * Compare two objects for DArray: sort.
     *
     * @param a first object
     * @param b second object
     * @return true if b should appear before a in the sorted list.
     */
    public boolean compare(Object a, Object b) {
      Renderable ra = (Renderable)a;
      Renderable rb = (Renderable)b;
      return (rb.zSortValue < ra.zSortValue);
    }

}