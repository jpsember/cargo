package Cargo;
import java.awt.Color;
import myUtils.*;

public class CargoItem {
  private static int colorCounter;

  public CargoItem(int w, int h, int id) {
    width = w;
    height = h;
    this.id = id;
    final Color[] colors = {
        Color.blue,
        Color.red,
        Color.lightGray,
        new Color(50,180,50),
        Color.orange,
        new Color(80,50,50),
    };
    color = colors[id % colors.length];

  }
  public void plot(myPanel panel) {
    panel.setColor(color);
    int x1 = x1(), y1=y1(), x2=x2(), y2=y2();

    panel.fillRect(x1,y1,x2-x1,y2-y1);
    panel.setColor(Color.black);
    panel.drawRect(x1,y1,x2-x1,y2-y1,0);
    if (false) {
      panel.setColor(Color.white);
    panel.drawString(""+id,(x1+x2)/2,(y1+y2)/2);
    }
  }

  public int x1() {return x;}
  public int y1() {return y;}
  public int x2() {return x + (rotated ? height : width);}
  public int y2() {return y + (rotated  ? width : height);}
  public void place(int x, int y) {
    this.x = x;
    this.y = y;
  //  this.rotated = rotated;
  }
  public void setRotate(boolean f) {
    rotated = f;
  }
  public int getRotWidth() {return rotated ? height : width;}
  public int getRotHeight() {return rotated ? width : height;}

  public int getWidth() {return width;}
  public int getHeight() {return height;}
  public int area() {return width * height;}

  private Color color;
  private int width, height;
  private int x,y;
  private boolean rotated;
  public int id;
}