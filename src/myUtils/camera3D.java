package myUtils;
import java.awt.*;
import java.awt.event.*;
public class camera3D
    implements MouseMotionListener, MouseListener, Globals {

  public static final int
      CA_NONE = 0
      , CA_MOVE = 1
      , CA_ZOOM = 2
      , CA_MOVEPLANAR = 3
      , CA_MOVEVERT = 4

      , PARMS = 6
      ;

  private view3D view;

  public void interp(double[] camParms, int v0, int v1, double f) {
    f = math.nonLinearInterpValue(f);

    final double[] ip = new double[camera3D.PARMS];
    for (int i = 0; i < camera3D.PARMS; i++) {
      double a = camParms[v0 * camera3D.PARMS + i],
          b = camParms[v1 * camera3D.PARMS + i];
      if (i == 3 || i == 4) {
        // Angles must be handled specially.
        double adiff = math.normalizeAngle(b-a);
        ip[i] = math.normalizeAngle(a + adiff * f);
      }  else

        ip[i] = a + (b - a) * f;
    }

    setParameters(ip, 0);
  }


  public camera3D(view3D view) {
    this.view = view;
    reset();
  }

//  public void setFocus(double x, double y, double z) {
//    focalPoint.set(x,y,z);
//  }

  public void look() {
    double dist = zoom * Math.cos(inclineAngle);
    double z = Math.cos(planarAngle) * dist;
    double x = Math.sin(planarAngle) * dist;
    double y = Math.sin(inclineAngle) * zoom;

    cameraPos.set(x + focalPoint.x, y + focalPoint.y, z + focalPoint.z);
  }

  public void mouseReleased(MouseEvent e) {
    Point pt = e.getPoint();
    //System.out.println("getModifiers = "+e.getModifiers());
    if (tools.button1(e)) {
//      case MouseEvent.BUTTON1:
        if (action == CA_MOVE || action == CA_MOVEPLANAR) {
          action = CA_NONE;
        }
    } else {
        if (action == CA_ZOOM || action == CA_MOVEVERT)
          action = CA_NONE;
    }
  }


  public void mousePressed(MouseEvent e) {
    boolean shifted =  (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (e.getModifiers() & InputEvent.CTRL_MASK) != 0;
    boolean alt = (e.getModifiers() & InputEvent.ALT_MASK) != 0;

//System.out.println("mousePress, mod="+e.getModifiers()+", shifted="+shifted);
    Point pt = e.getPoint();
    if (tools.button1(e)) {
        if (action == CA_NONE) {
          if (shifted && ctrl && alt) {
            reset();
            view.repaint();
          } else
          if (shifted) {
            display();
          } else

          if (ctrl) {
            action = CA_MOVEPLANAR;
            mouseStart = pt;
            startFocus = new FPoint3(focalPoint);
          } else {
            action = CA_MOVE;
            mouseStart = pt;
            startPlanarAngle = planarAngle;
            startInclineAngle = inclineAngle;
          }
        }
    }
        else {

        if (action == CA_NONE) {
          if (ctrl) {
            action = CA_MOVEVERT;
            mouseStart = pt;
            startFocus = new FPoint3(focalPoint);
          } else {
            action = CA_ZOOM;
            mouseStart = pt;
            startZoom = zoom;
            zoomSpeed = zoom / 100;
          }
        }
        }
  }
  public void reset() {
    action = CA_NONE;
    zoom = view.distanceToProjectionPlane();
    planarAngle = 0;
    inclineAngle = 0;
    focalPoint.set(0, 0, 0);
  }


  public void mouseMoved(MouseEvent e) {
  }

  private Point mStart = new Point();
  private double camYMove, camXMove;
  public void mouseDragged(MouseEvent e) {

    final double CAM_PLANAR_SPEED = (2 * PI / 300);
    double CAM_MOVE_SPEED = view.scaleValue();

    Point pt = e.getPoint();
    switch (action) {
      case CA_MOVE: {
        planarAngle = startPlanarAngle - (pt.x - mouseStart.x) * CAM_PLANAR_SPEED;
        inclineAngle =
            math.clamp(
            startInclineAngle + (pt.y - mouseStart.y) * CAM_PLANAR_SPEED,
            -PI / 2,
            PI / 2);
        view.repaint();
      }
      break;
      case CA_MOVEPLANAR: {
        final FPoint3 w = new FPoint3(), w2 = new FPoint3();
        w.z = -(pt.y - mouseStart.y) * CAM_MOVE_SPEED;
        w.x = -(pt.x - mouseStart.x) * CAM_MOVE_SPEED;

        Matrix m = Draw3D.rotateY(planarAngle);
        Draw3D.apply(m,w,w2);
        FPoint3.add(w2,startFocus,focalPoint);
        view.repaint();
      } break;

      case CA_MOVEVERT: {
        double y = (pt.y - mouseStart.y) * CAM_MOVE_SPEED;
        double ny = startFocus.y + y;
        focalPoint.y = Math.max(0, ny);
        view.repaint();

     } break;
      case CA_ZOOM: {
        int dist = pt.y - mouseStart.y;
        zoom = math.clamp(
            startZoom + dist * zoomSpeed, .05, 5000.0);
        view.repaint();
      }
      break;
    }
  }
  public Matrix matrix() {

    final Matrix dest = Draw3D.identity();
//    worldToView = Draw3D.identity();
//    worldToView.name("World -> View transformation");

//    Matrix cam = camera.matrix();
    Matrix mCamPos = Draw3D.translationMatrix(cameraPos, true);
    mCamPos.name("Camera translation");
    Matrix mCamRotY = Draw3D.rotateY( -1 * planarAngle);
    mCamRotY.name("Camera rotation Y");
    Matrix mCamRotX = Draw3D.rotateX(inclineAngle);
    mCamRotX.name("Camera rotation X'");
//    mCamPos.display(); mCamRotY.display(); mCamRotX.display();

    Draw3D.mult(mCamRotX, mCamRotY, dest);
    Draw3D.mult(dest, mCamPos, dest);
    return dest;
  }

  public void addMouseControl(Component panel) {
    panel.addMouseListener(this);
    panel.addMouseMotionListener(this);
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  // current camera action (CA_xx)
  private int action;
  // position at start of CA_MOVE
  private Point mouseStart;
  // zoom factor at start of CA_ZOOM
  private double startZoom;
  private FPoint3 startFocus;
  // speed of zoom
  private double zoomSpeed;
  // rotation at start of CA_MOVE
  private double startPlanarAngle;
  // rotation at start of CA_MOVE
  private double startInclineAngle;

  // position of camera
  private FPoint3 cameraPos = new FPoint3();

  // focal point of camera
  private FPoint3 focalPoint = new FPoint3();
  private double planarAngle;
  private double inclineAngle;
  // current zoom factor
  private double zoom;
  public void display() {
    System.out.println("// camera3D parameters:");
    System.out.print("setParameters(");
    d(focalPoint.x,true);
    d(focalPoint.y,true);
    d(focalPoint.z,true);
    d(planarAngle,true);
    d(inclineAngle,true);
    d(zoom,false);
    System.out.println(");");
  }

  public void getParameters(double[] a, int set) {
    int i = set * PARMS;
    a[i+0] = focalPoint.x;
    a[i+1] = focalPoint.y;
    a[i+2] = focalPoint.z;
    a[i+3] = planarAngle;
    a[i+4] = inclineAngle;
    a[i+5] = zoom;
  }

  public void setParameters(double[] array, int set) {
    int i = set * PARMS;
    setParameters(array[i+0],
                  array[i+1],array[i+2],
                  array[i+3],array[i+4],array[i+5]);
  }

  public void setParameters(double fx, double fy, double fz,
                            double pl, double incl, double zoom) {
    focalPoint.set(fx,fy,fz);
    planarAngle = pl;
    inclineAngle = incl;
    this.zoom = zoom;
    view.repaint();
  }

  private static void d(double d, boolean comma) {
    double trim = Math.round(d * 10000) / 10000.0;
    System.out.print(trim);
    if (comma)
      System.out.print(',');
  }

}