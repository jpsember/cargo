package myUtils;

//import java.awt.*;

public class MeshUtils
    implements MeshConst {
  private static final int wheel[] = {
      SCALE, 50,
      94, 0,
      88, 30,
      75, 54,
      54, 75,
      30, 88,
      0, 94,
      -30, 88,
      -54, 75,
      -75, 54,
      -88, 30,
      -94, 0,
      -88, -30,
      -75, -54,
      -54, -75,
      -30, -88,
      0, -94,
      30, -88,
      54, -75,
      75, -54,
      88, -30,
      FACES,
// Faces
      COLOR, BROWN, 90,
      2, 8, 12, 2, 12, 18,
      0, 1, 19,
      1, 2, 18, 1, 18, 19,
      4, 5, 6,
      3, 4, 6, 3, 6, 7, 2, 3, 7, 2, 7, 8,
      9, 10, 11, 8, 9, 11, 8, 11, 12,
      14, 15, 16, 14, 16, 17, 14, 17, 13, 13, 17, 18, 13, 18, 12,
      COLOR, BROWN, 60,
      LIFT, -47, 47,
      SCALE, 21,
      ENDSCRIPT

  };

  public static Mesh testMesh(int code, view3D view) {
    Mesh mesh = new Mesh();
    switch (code) {

      case TEST_WHEEL:
        mesh.read(wheel, null);
        Draw3D.rotateY(mesh.getMatrix(), Math.PI);
        break;

      case TEST_J: {
        final int[] scr = {
            1, 5, 3, 5, 3, 3, 6, 3,
            6, 7, 5, 7, 5, 8, 9, 8,
            9, 7, 8, 7, 8, 3, 7, 1, 2, 1, 1, 3, 1, 5

            , 0, 2, 10, 2, 10, 8, 0, 8, 0, 2,
        };
        FPoint3 v0 = null;
        double scl = 1 / 10.0;
//    double scl = view.frustumWidth() / 10.0;

        int j = 0;
        for (int i = 0; i < scr.length; i += 2, j++) {
          FPoint3 pt = new FPoint3(
              (scr[i] - 5) * scl,
              (scr[i + 1] - 5) * scl,
              0);
          Vert3D v = mesh.addVert(pt);

//      if (i == 0)
          //      mesh.addFace(0,1,2);

          if (i > 0)
            mesh.addLine(j - 1, j);
        }
      }

      break;

      case TEST_GRID: {
        mesh.setColor(DARKGRAY,1.0);
        final int size = 10;
        double y0 = -size * .5;
        double y1 = size * .5;
        for (int i = 0; i <= size; i++) {
            double x = i + y0;
            mesh.addVert(x,y0,0);
            mesh.addVert(x,y1,0);
            mesh.addVert(y0,x,0);
            mesh.addVert(y1,x,0);
            mesh.addLine(0 + i*4, 1 + i*4);
            mesh.addLine(2 + i*4, 3 + i*4);
          }
      }
      break;

      case TEST_PARABOLOID: {
        int size = 10;
        for (int i = 0; i <= size; i++) {
          for (int j = 0; j <= size; j++) {
            double x = (j - size / 2) * .1;
            double y = (i - size / 2) * .1;
            double z = (x * x + y * y); // + .05;

            mesh.addVert(x, z, y);
//           mesh.addVert(z,y,x);
          }
        }
        mesh.setFaceFlags(FF_BORDER);
//       mesh.setFaceFlags(FF_WIREFRAME);
        mesh.setColor(BROWN,.5);
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            int v0 = i * (size + 1) + j;

            Face f = mesh.addFace(v0, v0 + (size + 1), v0 + 1);
            mesh.addFace(v0 + 1, v0 + (size + 1), v0 + (size + 1) + 1);
          }
        }
        MeshUtils.applyShading(mesh,view);
      }
      break;
    }
    return mesh;
  }

  /*	'Lift' a flat mesh expressed in the x/y plane into 3 dimensions
          by duplicating each vertex and face at a different z-coordinate,
          then adding faces connecting the upper and lower vertices to
          'fill in' the volume created between the top and bottom ends.
          Any faces created will have the current mesh face color.
          > m			Mesh to 'lift'
          > zBottom	z-coordinate to set bottom end at
          > zTop		z-coordinate to set top end at
   */
  public static void make3D(Mesh m, double zBottom, double zTop) {

    DArray fOld = m.elements; //getFaceArray();
    DArray vOld = m.vertices;

    DArray faceA = new DArray();
    DArray vertA = new DArray();

    int vc = vOld.size();
    int fc = fOld.size();

    int i;
    for (int pass = 0; pass < 2; pass++) {
      for (i = 0; i < vc; i++) {
        FPoint3 v = (FPoint3) (vOld.get(i));
        FPoint3 w = new FPoint3(v);
        if (pass == 0)
          w.z = zTop;
        else
          w.z = zBottom;
        vertA.add(w);
      }

      for (i = 0; i < fc; i++) {
        Face w = (Face) fOld.get(i);
        if (pass == 1) {
          int temp = w.v1;
          w.v1 = w.v2 + vc;
          w.v2 = temp + vc;
          w.v0 += vc;
        }
        faceA.add(w);
      }
    }

    for (i = 0; i < vc; i++) {
      int v0 = i;
      int v1 = (i + 1) % vc;
      int w0 = v0 + vc;
      int w1 = v1 + vc;
      Face w = new Face();
      w.v0 = v1;
      w.v1 = v0;
      w.v2 = w0;
      w.flags = m.getFaceFlags();
      faceA.add(w);

      Face w2 = new Face();
      w2.v0 = v1;
      w2.v1 = w0;
      w2.v2 = w1;
      w2.flags = w.flags;
      faceA.add(w2);
    }
    m.elements = faceA;
    m.vertices = vertA;
  }

  /**
   * Apply shading to mesh; sets intensity of each face's color
   * based on angle face normal makes with light source
   * @param view containing light source
   */
  private static final FPoint3 faceNormal = new FPoint3(),
      sVect = new FPoint3(), tVect = new FPoint3(),
      cProd = new FPoint3();

  public static void applyFlatColor(Mesh m, int color, double level) {
    for (int i = 0; i < m.elements.size(); i++) {
      Renderable e = m.elem(i);
      e.setColor(color,level);
    }

  }

  public static void applyShading(Mesh m, view3D view) {

    m.ensureVertBuffers();
//    double[] viewVert = m.viewVert;

    m.transformVertices(m.getMatrix(), null);
    for (int i = 0; i < m.elements.size(); i++) {
      Renderable e = m.elem(i);
      if (e.type != RENDERABLE_FACE)
        continue;

      Face f = (Face) e;
      {
        // Determine lighting value from normal to face.

        // Calculate index of start of x,y,z coordinates for each vertex.
        int a = f.v0 * 3, b = f.v1 * 3, c = f.v2 * 3;

        // Set s and t to the vectors relative to vertex c.
        sVect.x = m.viewVert[a + 0] - m.viewVert[c + 0];
        sVect.y = m.viewVert[a + 1] - m.viewVert[c + 1];
        sVect.z = m.viewVert[a + 2] - m.viewVert[c + 2];
        tVect.x = m.viewVert[b + 0] - m.viewVert[c + 0];
        tVect.y = m.viewVert[b + 1] - m.viewVert[c + 1];
        tVect.z = m.viewVert[b + 2] - m.viewVert[c + 2];

        // Calculate the cross product to give us the normal to the face.

        FPoint3.crossProduct(sVect, tVect, cProd);
        cProd.normalize();
        // Calculate dot product with light direction to
        // give us the cosine of the angle between them.
        // Negate the cosine, since the light source is pointing
        // towards the face, while its normal is pointing away.

        double sine = -FPoint3.dotProduct(cProd, view.lightDir());
        double currLevel = f.getShade() * .75;
        if (sine > 0) {
          currLevel = (1.0 + sine) * currLevel;
        }

        f.setShade( (int) currLevel);
      }
    }
  }


}