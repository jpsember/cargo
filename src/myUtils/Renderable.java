package myUtils;
import java.awt.*;

abstract public class Renderable implements MeshConst  {

  public int flags;
  public final void setColor(int c, double intensity) {
    flags &= ~(RF_COLOR|RF_SHADE);
    flags |= (c << RS_COLOR) | (math.clamp((int)(intensity * COLOR_LEVELS),0,COLOR_LEVELS-1) << RS_SHADE);
  }
  public final int getShade() {
    return (flags & RF_SHADE) >> RS_SHADE;
  }

  public final void setShade(int level) {
    level = math.clamp(level, 0, COLOR_LEVELS-1);
    flags = (flags & ~RF_SHADE) | (level << RS_SHADE);
  }

  public final void setVisibility(boolean visible) {
    flags = (flags & ~RF_HIDDEN) | (visible ? 0 : RF_HIDDEN);
  }

  public final boolean visible() {
    return (flags & RF_HIDDEN) == 0;
  }

  protected final void setColor(Graphics g) {
    Colors.set(g, flags & RF_COLOR, getShade());
  }

  /**
   * Transform an element for the view
   * @param view : view to transform for
   * @param viewVert : array of vertices transformed to view space
   * @param panelVert : array of vertices projected to panel space
   */
  abstract public void transform(view3D view, double[] viewVert, int[] panelVert);
  /**
   * Render the object
   * @param view : the current view
   */
  abstract  public void render(view3D view);

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("rt="+Fmt.f(type,2));
    sb.append(visible() ? " Vis" : " Hid");
    return sb.toString();
  }

  /**
   * Determine object's z-sort value.
   * @return the value to use for bucket sorting
   */
  public double zSortValue;
  public int type;
  public boolean deleted() {
    return (type == RENDERABLE_DELETED);
  }
  public void delete() {
    type = RENDERABLE_DELETED;
  }
  public int index;
}