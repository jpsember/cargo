package myUtils;

public class Mesh
    implements Globals, MeshConst {

  public Mesh() {
  }

  private static double ANG(int n) {
    return (n * ( (2 * PI) / 0x10000));
  }

  public void removeElement(Renderable e) {

    // Add index of this element to the list of deleted elements.
    deleteList.addInt(e.index);
    e.delete();
  }

  /**
   *   Read vertices and faces into model from a script.
   * @param model script (vertices, faces, and commands, ending with ENDSCRIPT)
   * @param library optional array of meshes to use as library for child meshes
   */
  public void read(int[] model, DArray library) {
    int src = 0;
    Face f = new Face();
    int fOrder = 0;
    int vOffset = 0;
    double zValue = 0;

    boolean auxMat = false;
    boolean includeZ = false;
    double orgX = 0;
    double orgY = 0;

    final int S_VERT = 0
        , S_FACE = 1
        , S_DONE = 2;

    final Matrix workMat = Draw3D.identity();
    workMat.name("Mesh.read work matrix");

    int state = S_VERT;
    while (state != S_DONE && src < model.length) {

      Matrix matrix = (auxMat ? workMat : this.matrix);

      int cmd = model[src];
      switch (cmd) {
        case AUXMAT:
          auxMat = true;
          src++;
          break;
        case CHILD:
          addChild( (Mesh) library.get(model[src + 1]), matrix);
          src += 2;
          break;
        case ORIGIN:
          orgX = model[src + 1];
          orgY = model[src + 2];
          src += 3;
          break;
        case WITHZ:
          includeZ = true;
          src++;
          break;
        case VOFFSET:
          vOffset = model[src + 1];
          src += 2;
          break;
        case VERTS:
          state = S_VERT;
          src++;
          break;
        case ENDSCRIPT:
          state = S_DONE;
          src++;
          break;
        case FACES:
          state = S_FACE;
          src++;
          break;
        case CW:
          fOrder = 1;
          src++;
          break;
        case CCW:
          fOrder = 0;
          src++;
          break;
        case COLOR:
          setColor(model[src + 1], model[src + 2] * (1.0 / 100));
          src += 3;
          break;
        case LIFT:
          MeshUtils.make3D(this,model[src + 1], model[src + 2]);
          src += 3;
          break;
        case SCALE:
          Draw3D.scale(matrix, model[src + 1] / 1000.0);
          src += 2;
          break;
        case SCALEI:
          Draw3D.scale(matrix, model[src + 1] / 1000.0, model[src + 2] / 1000.0,
                       model[src + 3] / 1000.0);
          src += 4;
          break;
        case ROTX:
          Draw3D.rotateX(matrix, ANG(model[src + 1] << 8));
          src += 2;
          break;
        case ROTY:
          Draw3D.rotateY(matrix, ANG(model[src + 1] << 8));
          src += 2;
          break;
        case ROTZ:
          Draw3D.rotateZ(matrix, ANG(model[src + 1] << 8));
          src += 2;
          break;
        case TL:
          Draw3D.translate(matrix, model[src + 1], model[src + 2],
                           model[src + 3]);
          src += 4;
          break;
        case SETZ:
          zValue = model[src + 1];
          src += 2;
          break;
        default:
          switch (state) {
            case S_VERT: {
              double x = model[src + 0];
              double y = model[src + 1];
              double z = 0;
              if (includeZ) {
                z = model[src + 2];
                src += 3;
              }
              else {
                z = zValue;
                src += 2;
              }
              x -= orgX;
              y -= orgY;
              addVert(x, y, z);
            }
            break;
            case S_FACE: {
              int v0 = model[src] + vOffset;
              int v1 = model[src + 1] + vOffset;
              int v2 = model[src + 2] + vOffset;
              if (fOrder == 0) {
                f.v0 = v0;
                f.v1 = v1;
                f.v2 = v2;
              }
              else {
                f.v0 = v0;
                f.v1 = v2;
                f.v2 = v1;
              }
              src += 3;

              f.flags = faceFlags;
              addFace(f.v0, f.v1, f.v2, f.flags);
            }
            break;
          }
          break;
      }
    }
  }

  /*	Set the color for the next face to be added to the mesh.
       > index		index of color (see MeshColor.cpp); its intensity is set to 1.0
   */
  public void setColor(int index) {
    setColor(index, 1.0);
//    faceColor = index;
//    faceIntensity = 1.0;
  }

  /*	Set the color for the next face to be added to the mesh.
          > index		index of color (see MeshColor.cpp)
          > intensity	intensity value (1.0 = normal)
   */
  public void setColor(int index, double intensity) {
    faceColor = index;
    faceIntensity = intensity;
    int fi = math.clamp( (int) (faceIntensity * 64), 0, 63);

    faceFlags = (faceColor | (fi << 8)) | (faceFlags & ~ (RF_COLOR | RF_SHADE));
  }

  /**
  /* Structure for describing child meshes
  */
   class ChildMeshRec {
     public ChildMeshRec(Mesh mesh, Matrix mat) {
       ptr = mesh;
       m = mat;
     }

     public Mesh ptr;
     public Matrix m;
   }

  /*	Add a child mesh to this mesh's hierarchy.
          > child		child mesh
          > m			transformation matrix to apply before plotting child
   */
  public void addChild(Mesh child, Matrix m) {
    ChildMeshRec r = new ChildMeshRec(child, m);
    childMesh.add(r);
  }

  public void plot(view3D view) {
    view.pushMatrix();
    plotAux(view);
    view.popMatrix();
  }

  /*	Plot the mesh with openGL, using current MODELVIEW matrix; apply
          this mesh's matrix, and don't save or restore the existing matrix
   */
  public void plotAux(view3D view) {
    view.multMatrix(matrix);
    Matrix m = view.matrix();
    transformVertices(m, view);

    plotElements(view);

    for (int i = 0; i < childMesh.size(); i++) {
      ChildMeshRec cm = (ChildMeshRec) childMesh.get(i);
      view.pushMatrix();
      view.multMatrix(cm.m);
      cm.ptr.plotAux(view);
      view.popMatrix();
    }
  }

  public Vert3D vert(int i) {
    return (Vert3D) vertices.get(i);
  }

  void ensureVertBuffers() {
int nVert =    vertices.size();
    int pvLen = nVert * 2;
    if (panelVert == null || panelVert.length < pvLen) {
      viewVert = new double[nVert * 3];
      panelVert = new int[pvLen];
    }
  }

  /**
   * Transform mesh's vertices
   * @param m matrix to transform by
   * @param view if not null, transforms to panel space based
   *   on this view
   */
  void transformVertices(Matrix m, view3D view) {
    // Transform all the vertices at one go.

    int nVert = vertices.size();
    ensureVertBuffers();
    for (int i = 0, j = 0, k = 0; i < nVert; i++, j += 3, k += 2) {
      FPoint3 w = vert(i).pt;
      //(FPoint3)vertices.get(i);
      final FPoint3 v = new FPoint3();
      final Point2 p = new Point2();
      Draw3D.apply(m, w, v);
      viewVert[j + 0] = v.x;
      viewVert[j + 1] = v.y;
      viewVert[j + 2] = v.z;
      if (view != null) {
        view.viewToPanel(v, p);
        panelVert[k + 0] = p.x;
        panelVert[k + 1] = p.y;
      }
    }
  }

  public void plotElements(view3D view) {
    for (int pass = 0; pass < 2; pass++) {
      DArray list = (pass == 0) ? vertices : elements;

      for (int i = 0; i < list.size(); i++) {
        Renderable e = (Renderable) (list.get(i));
        if (e.deleted()
            || !e.visible()
            ) continue;
        e.transform(view, viewVert, panelVert);
      }
    }
  }

  /*	Get pointer to mesh's transformation matrix
          < Matrix *		pointer to matrix
   */
  public Matrix getMatrix() {
    return matrix;
  }

  /*	Replace mesh's transformation matrix with another
          > src		Matrix to replace with (it's copied to the existing matrix)
   */
  public void setMatrix(Matrix src) {
    Matrix.copy(src, matrix);
  }

      /*	Add a face to the mesh.  The current face flags are used.  Use setColor(...)
         to change the color of the face.
         > v0,v1,v2	vertex indices for face
   */
  public Face addFace(int v0, int v1, int v2) {
    return addFace(v0, v1, v2, faceFlags);
  }

  public Circle3D addCircle(int v, double r) {
    Circle3D c = new Circle3D(v,r,faceFlags);
    addElem(c);
    return c;
  }

  public void setVertRadius(double r) {
    vertRadius = r;
  }
  public double vertRadius() {
    return vertRadius;
  }
  private double vertRadius;

  /*	Add vertex to mesh.
          > x,y,z		coordinates of vertex
   */
  public Vert3D addVert(double x, double y, double z) {
    Vert3D v = new Vert3D(x, y, z, vertices.size(), vertRadius);
    vertices.add(v);
    return v;
  }

  public Vert3D addVert(FPoint3 pt) {
    Vert3D v = new Vert3D(pt.x, pt.y, pt.z, vertices.size(), vertRadius);
    vertices.add(v);
    return v;
  }

  /*	Get the current face flags (see Face structure)
          < int		current face flags (FACEF_xx)
   */
  public int getFaceFlags() {
    return faceFlags;
  }

  public void setFaceFlags(int f) {
    faceFlags = f;
  }

  /*	Add a face to the mesh.
          > v0,v1,v2	vertex indices
          > flags		face flags (see FACEF_xx)
   */
  public Face addFace(int v0, int v1, int v2, int flags) {
    Face f = new Face(v0, v1, v2, flags);
    addElem(f);
return f;
  }

  public Line3D addLine(int v0, int v1) {
    Line3D line = new Line3D(v0, v1, faceFlags);
    addElem(line);
    return line;
  }

  private void addElem(Renderable r) {
    r.index = elements.size();
    if (deleteList.size() > 0) {
      r.index = deleteList.getInt(deleteList.size() - 1);
      deleteList.remove(deleteList.size() - 1);
      elements.set(r.index, r);
    }
    else {
      elements.add(r);
    }
  }

  /*	Transform a face through the mesh's transformation matrix
          > f		face to transform
          < vert	where to store the three transformed vertices
   */
  public void transformFace(Face f, FPoint3[] vert) {
    FPoint3 v1 = vert(f.v0).pt,
        v2 = vert(f.v1).pt,
        v3 = vert(f.v2).pt;
    Draw3D.apply(matrix, v1, vert[0]);
    Draw3D.apply(matrix, v2, vert[1]);
    Draw3D.apply(matrix, v3, vert[2]);
  }

  public Renderable elem(int i) {
    return (Renderable) elements.get(i);
  }

  public void scale(double s) {
    Draw3D.scale(matrix, s);
  }

  /**
   * Note: package visibility for these.
   */
  DArray elements = new DArray();
   DArray vertices = new DArray();
   DArray childMesh = new DArray();

   public int getVertCount() {
     return vertices.size();
   }

   public int getRenderableCount() {
     return elements.size();
   }

  // Array of view and panel space coordinates for each vertex.
  // [0..2] : view x,y,z
  // [3..4] : panel x,y
  double[] viewVert;
  private int[] panelVert;

  private Matrix matrix = Draw3D.identity();
  private int faceColor;
  private double faceIntensity;
  private int faceFlags;
  private DArray deleteList = new DArray();
}