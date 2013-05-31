package myUtils;
import java.awt.*;
import java.awt.event.*;
public class Test3d extends FrameApplet {
  public void openComponents(Container c) {
    TestBed tp = new TestPanel(c);
  }
}

class TestPanel
    extends TestBed {
    public TestPanel(Container c) {
      super( "Test applet for myUtils");

      Panel p = new Panel(new BorderLayout());
      p.add(new WorkPanel(),"Center");

      construct(c, p, this, buttonNames, list0);
    }

    private static final int
        B_DELETEPT = 0
        ;
    private static final String[] buttonNames = {
        ".D D)elete point",
    };
    public final int[] list0 = {
        B_DELETEPT,
    };

    public void prepared() {

    }
}
 class WorkPanel extends myPanel implements MeshConst {

   private Mesh mesh;
   private final int wheel[] = {
       SCALE,50,
   94,0,
   88,30,
   75,54,
   54,75,
   30,88,
   0,94,
   -30,88,
   -54,75,
   -75,54,
   -88,30,
   -94,0,
   -88,-30,
   -75,-54,
   -54,-75,
   -30,-88,
   0,-94,
   30,-88,
   54,-75,
   75,-54,
   88,-30,
   FACES,

   // Faces
   COLOR,BROWN,90,
   2,8,12, 2,12,18,
   0,1,19,
   1,2,18,1,18,19,
   4,5,6,
   3,4,6,3,6,7,2,3,7,2,7,8,
   9,10,11,8,9,11,8,11,12,
   14,15,16,14,16,17,14,17,13,13,17,18,13,18,12,
   COLOR,BROWN,60,
   LIFT,-47,47,
   SCALE,21,
   ENDSCRIPT

   };


   private view3D view;
   public WorkPanel() {
     super("3D Panel",STYLE_ETCH,Color.lightGray);

     // Set up a 3d view.
     view = new view3D(this);
     view.addMouseCameraControl();
     camera3D c = view.camera();
     c.setParameters(-0.8732,0.532,1.0734,-0.6912,0.2304,1.1196);
//     c.setFocus(-.1,.2,.1);
     mesh = new Mesh();
     if (false) {
       mesh.read(wheel,null);
       Draw3D.rotateY(mesh.getMatrix(),Math.PI);
     } else {
       int size = 12;
       for (int i = 0; i <= size; i++) {
         for (int j = 0; j <= size; j++) {
           double x = (j - size/2) * .1;
           double y = (i - size/2) * .1;
           double z = (x*x + y*y);// + .05;

           mesh.addVert(x,z,y);
//           mesh.addVert(z,y,x);
         }
       }
       mesh.setFaceFlags(FF_BORDER);
//       mesh.setFaceFlags(FF_WIREFRAME);
       for (int i = 0; i < size; i++) {
         for (int j = 0; j < size; j++) {
           int v0 = i * (size+1) + j;
           mesh.setColor(BROWN,.5);

           mesh.addFace(v0,v0+(size+1),v0+1);
           mesh.addFace(v0+1,v0+(size+1),v0+(size+1)+1);
         }
       }
       MeshUtils.applyShading(mesh,view);
     }
   }

   public void paintInterior(Graphics g) {
     super.paintInterior(g);
     view.renderBegin(g);
     mesh.plot(view);
     view.renderEnd();
   }


 }
