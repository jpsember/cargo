package Cargo;
import myUtils.*;
import java.awt.*;

public class FloorPlan {

  public FloorPlan(int w, int h) {
    fwidth = w;
    fheight = h;
  }

  public int getUsedWidth() {return usedWidth;}
  public int getUsedHeight() {return usedHeight;}
  public int getOptimalArea() {return itemsArea;}
  public int getOverflow() {return overflowArea;}

  public void clear() {
    items.clear();
    overflowArea = itemsArea = 0;
    usedWidth = usedHeight = 0;
    overflowCount = 0;
  }
public void prob() {
    System.out.println("problem, itemsStored="+itemsStored());
    System.out.println(" overflowCount="+overflowCount);
    System.out.println(" items area="+itemsArea);
}

  private static double VXM = 6400,
      VYM = 6400/5;

  public void plot(myPanel panel) {
    panel.setColor(Color.magenta);

//    panel.setScale(scale);
    for (int j = 1; j <= 2; j++)
      panel.drawRect(0,0,VXM,VYM, j);
    for (int i = 0; i < itemsStored(); i++) {
      CargoItem item = getItem(i);
      item.plot(panel);
    }
  //  panel.clearScale();
  }


  /**
   * Attempt to store an item in the plan.
   * @param item item to store
   */
  public void store(CargoItem item) {
    int x = 0;
    int w = item.getRotWidth();
    int h = item.getRotHeight();

    itemsArea += w*h;
    int yMax = fheight - h;
    int xMax = fwidth - w;
    boolean stored = false;

if (disp) System.out.println("Store "+item.id+" w="+w+",h="+h);

yLoop:
    while (x <= xMax) {
      int y = 0;
      int resetRow = xMax+1;
xLoop:      while (y <= yMax) {

        int x2 = x + w;
        int y2 = y + h;
if (disp) System.out.println(" scan x="+x+",y="+y+", resetRow="+resetRow);
        // Will item fit at x,y?
        for (int i = 0; i < items.size(); i++) {
          CargoItem item2 = (CargoItem)items.get(i);
          if (item2.x1() >= x2) continue;
          if (item2.x2() <= x) continue;

          if (item2.y1() >= y2) continue;
          if (item2.y2() <= y) continue;
          // This item overlaps ours.
          if (item2.x2() < resetRow)
            resetRow = item2.x2();
          y = item2.y2();
if (disp)          System.out.println("  overlaps item at "+item2.x1()+","+item2.y1());
          continue xLoop;
        }
        // Found space for item.
if (disp)      System.out.println("   placing at "+x+","+y);
        item.place(x,y);
        items.add(item);
        stored = true;

        if (x2 > usedWidth)
          usedWidth = x2;
          if (y2 > usedHeight)
            usedHeight = y2;

        break yLoop;
      }
      x = resetRow;
    }
    if (!stored) {
      overflowCount++;
      overflowArea += item.area();
    }
  }

public boolean disp = false;

  public int itemsStored() {return items.size();}
  public CargoItem getItem(int i) {return (CargoItem)items.get(i);}

  public int getWidth() {return fwidth;}
  public int getHeight() {return fheight;}

  private int overflowArea;
  private int fwidth, fheight;
  private int usedWidth, usedHeight;
  private int itemsArea;
  private int overflowCount;
  private double scale = 1.0;
  public void setScale(double factor) {
    scale = factor;
  }
  private DArray items = new DArray();
}