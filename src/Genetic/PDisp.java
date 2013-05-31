package Genetic;

import myUtils.*;
import java.awt.*;

public class PDisp
    extends myPanel
    implements Globals
{
  private static final int
      DEF_WIDTH = 200
      ,DEF_HEIGHT = 400
      ;

  private Point plotSize;
  private int nCells;
  private int popSize;
  private int pixelSize;
  private int genSep;
  private Population pop;
  private boolean vertical;
  private int drawnCells, drawnPop;

  public Dimension getPreferredSize() {
    return addBorders(plotSize);
  }

  public void paintInterior(Graphics g) {
    super.paintInterior(g);

    Point offset = centerRect(plotSize.x,plotSize.y);

    final Color[] colors = {
        Color.lightGray,
        Color.blue,
        Color.red,
        Color.green,
    };
    for (int i = 0; i < drawnPop; i++) {
      int y = i * (pixelSize+genSep);
      Genotype gen = pop.getGenotype(i);
      for (int j = 0; j < drawnCells; j++) {
        int x = j * pixelSize;

        int val = gen.getInt(j * 2, 2);
        g.setColor( colors[val]);

        int dx = offset.x + ( vertical ? y : x);
        int dy = offset.y + (vertical ? x : y);

        if (pixelSize > 1) {
            g.fillRect(dx,dy,pixelSize,pixelSize);
        } else {
          g.drawLine(dx,dy,dx,dy);
        }
      }
    }

  }

  public void setPop(Population p) {

    pop = p;
    nCells = p.getGenomeLength() / 2;
    popSize = p.getPopSize();

    vertical = nCells > popSize;

    int w = DEF_WIDTH, h = DEF_HEIGHT;

    if (vertical) {
      int t = w; w = h; h = t;
    }
    int w2 = w / nCells;
    int h2 = h / popSize - 1;

    pixelSize = math.clamp(Math.min(w2,h2),1,3);
    genSep = (pixelSize > 1) ? 1 : 0;

    drawnCells = math.clamp(w/pixelSize, 0, nCells);
    drawnPop =  math.clamp(h/(pixelSize+genSep), 0, popSize);

    plotSize = vertical ?
        new Point(drawnPop * (pixelSize+genSep),drawnCells * pixelSize)
        : new Point(drawnCells * pixelSize, drawnPop * (pixelSize+genSep));
  }

  public PDisp(Population p) {
    super("Genotypes",STYLE_ETCH,Color.white);
    setPop(p);
  }
}
