package myUtils;

public class MeshAnim
    implements Globals, MeshConst {

  private static final int
      ANIMLEN_HLVERT = TICK * 5
      , ANIMLEN_HLVERTOFF = TICK * 3
      , ANIMLEN_HLEDGE = TICK * 5
      , ANIMLEN_HLEDGEOFF = TICK * 3
      ;
  private static double
    HLVERT_RADIUS = 1.5
    ;
  /**
   * Constructor
   * @param m : mesh to animate
   */
  public MeshAnim(Mesh m, int startTime) {
    mesh = m;
    this.time = startTime;
    // Construct list of active animation elements
    list = new AnimElem(0, 0);
    list.next = list;

  }

public void setTime(int t) {
    time = t;
}

private static void dispList(AnimElem e) {
    System.out.print("List: ");
    AnimElem f = e;
    do {
      System.out.print(f.id+";");
      f = f.next;
    } while (f != e);
    System.out.println();
}

  public boolean finished() {
    return list == list.next;
  }
  private String name2;
  public void name(String s) {
    name2 = s;
  }
  static String name;

  /**
   *
   * @return true if animation is complete
   */
  public boolean update() {
    name = name2;
//    System.out.print("MeshAnim update, ");dispList(list);
    AnimElem prev = list;
    for (AnimElem e = prev.next;
         e != list;
         prev = e, e = e.next
         ) {
      int t = time - e.startTime;
     // System.out.println(" time=" + time + ", elem=" + e+" t="+t);
      if (t < 0)
        continue;
      boolean del = false;
//      System.out.println("processing");
      switch (e.code) {
        case ANIM_HLVERT1:
        case ANIM_HLVERT2: {
          final int len = ANIMLEN_HLVERT;
          double dist = t / (double) len;
          double s = 1.0 + ( (HLVERT_RADIUS - 1.0) * dist);
          if (e.code == ANIM_HLVERT1) {
            e.vertex.setColor(BLUERED, dist);
          }
          else {
            e.vertex.setColor(GREENBLUE, 1.0 - dist);
          }
          e.vertex.setRadius(mesh.vertRadius() * s);
          if (t == len) {
            del = true;
          }
        }
        break;
        case ANIM_HLVERT1OFF:
        case ANIM_HLVERT2OFF: {
          final int len = ANIMLEN_HLVERTOFF;

          double dist = 1.0 - t / (double) len;
          double s = 1.0 + ( (HLVERT_RADIUS - 1.0) * dist);
          if (e.code == ANIM_HLVERT1OFF) {
            e.vertex.setColor(BLUERED, dist);
          }
          else {
            e.vertex.setColor(GREENBLUE, 1.0 - dist);
          }
          e.vertex.setRadius(mesh.vertRadius() * s);
          if (t == len) {
            del = true;
          }
        }
        break;

        case ANIM_HLEDGE:
        case ANIM_HLEDGE2:
        {
          final int len = ANIMLEN_HLEDGE;
          double dist = t / (double) len;
          if (e.code == ANIM_HLEDGE) {
            e.line.setColor(
                GRAYGREEN, dist);
          } else {
          e.line.setColor(
              GRAYRED, dist);
          }
          if (t == 0) {
            e.grab();
            e.line.flags |= MeshConst.LF_THICK;
          }

//            System.out.println("anim type "+e.code+", t="+t+", THICK="+(e.line.flags & LF_THICK)+", line "+e.line);

          if (t == len) {
            del = true;
            e.release();
          }
        }
        break;

        case ANIM_HLEDGEOFF:
        case ANIM_HLEDGEOFF2:
        {
          if (t == 0) e.grab();
          final int len = ANIMLEN_HLEDGEOFF;
          double dist = 1.0 - t / (double) len;
          if (e.code == ANIM_HLEDGEOFF) {
            e.line.setColor(GRAYGREEN, dist);
          } else {
            e.line.setColor(GRAYRED, dist);
          }
          if (t == len) {
            del = true;
            e.release();
//            if ((e.line.flags & LF_THICK) != 0)
//              System.out.println("*** Removing THICK from "+e.line);
            e.line.flags &= ~MeshConst.LF_THICK;
          }
        }
        break;

        default:
          System.out.println("*** Unimplemented anim code: " + e.code);
          break;
      }
      if (del) {
//        System.out.println(" deleting element " + e + " by attaching " + prev +
  //                         " to " + e.next);
        prev.next = e.next;
        e = prev;
        //dispList(list);
      }
    }
    time++;
    return finished();
  }

  public void hlEdge(Line3D line, int prevStyle, int newStyle, int delay) {
    final int[] anims = {
        -1,
        ANIM_HLEDGE,
        ANIM_HLEDGE2,

        ANIM_HLEDGEOFF,
        -1,
        ANIM_HLEDGE2,

        ANIM_HLEDGEOFF2,
        ANIM_HLEDGE,
        -1,
    };
    int anim = anims[prevStyle * 3 + newStyle];
    if (anim < 0)
      return;

    AnimElem e = new AnimElem(anim, time + delay);
    e.line = line;
    addElement(e);
  }

  /**
   * Highlight a vertex.
   * @param vert : vertex to highlight
   * @param prevStyle : previous style of vertex (0:unhl; 1:hlA; 2:hlB)
   * @param newStyle : new style of vertex
   * @param startTime : time when animation is to begin, or 0 to start
   *  immediately
   */
  public void hlVert(Vert3D vert, int prevStyle, int newStyle, int startDelay) {

    final int[] anims = {
        -1,
        ANIM_HLVERT1,
        ANIM_HLVERT2,

        ANIM_HLVERT1OFF,
        -1,
        ANIM_HLVERT2,

        ANIM_HLVERT2OFF,
        ANIM_HLVERT2,
        -1,
    };
    int anim = anims[prevStyle * 3 + newStyle];
    if (anim < 0)
      return;

//    final int[] anims = {
//        ANIM_HLVERT1, ANIM_HLVERT2 };

    AnimElem e = new AnimElem(anim, time + startDelay);
    e.vertex = vert;
//    e.startRad = vert.getRadius();
    addElement(e);

  }

  private void addElement(AnimElem e) {
    e.next = list.next;
    list.next = e;
  }

  public int time() {
    return time;
  }

  private Mesh mesh;
  public AnimElem list;
  private int time;
}

class AnimElem {
  public String toString() {
    return "AnimElem id "+id+" code=" + code + ", startTime=" + startTime;
  }

  public void grab() {
//    System.out.println("Attempting to grab "+line);
//    tools.ASSERT(line.user == null,MeshAnim.name);
    line.user = this;
  }
  public void release() {
//    System.out.println("Releasing "+line);
//    tools.ASSERT(line.user == this,MeshAnim.name);
    line.user = null;
  }

  public AnimElem(int code, int startTime) {
    this.code = code;
    this.startTime = startTime;
  }
  private static int baseId = 0;
  public int id = ++baseId;
  public AnimElem next;
  public int code;
  public int startTime;
  public Vert3D vertex;
  public Line3D line;
  public double startRad;
}