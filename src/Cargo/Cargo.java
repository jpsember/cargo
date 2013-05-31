package Cargo;

import myUtils.*;
import Genetic.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Cargo
    extends FrameApplet
    implements Globals {
  private CargoPanel panel;

  public void openComponents(Container c) {
    panel = new CargoPanel(c);
  }
  public void stop() {
    panel.stop();
//    panel.thread = null;
  }
    }
  /*
public class Cargo
    extends Applet
    implements Globals
{

  private CargoPanel p;
  public void init() {
    setLayout(new BorderLayout());
    p = new CargoPanel(this);

  }

  public void destroy() {
    p.destroy();
  }

}
*/
class CargoPanel
    extends TestBed
    implements IGenEvaluator, GenGlobals, myUtils.Comparable {

  private static final int
      S_START = 2
      , S_RUNNING = 3

      ,ITEMS = 50
      ,POP_SIZE = 25
      ,CSIZE_MIN = 150
      ,CSIZE_MAX = 1000
      ;

  private static final int
      B_RESET = 0
      , B_RUN = 1
      , B_STEP = 2
      , B_STOP = 3
      , B_SORTED = 4
      , B_SEED = 5
      ;

  private static final String[] buttonNames = {
      ".M RandoM)ize",
      ".R R)un",
      ".S S)tep",
      ".X (X)Stop",
      ".H H)euristic ordering",
      ".I Change I)tems",
  };
  public final int[] list0 = {
      B_STEP,
      B_RUN,
      B_STOP,
      B_SORTED,
      B_SEED,
      B_RESET,
  };

  public static final double VXM = 6400,
      VYM = VXM/5;
  private myPanel pDisp;

  private eThread thread;

  public CargoPanel(Container c) {
    super("Cargo Plan");

    initItems(1965);
    initPop();

    thread = new eThread(this);

    Panel p = new Panel(new BorderLayout());

    p.add(this,"Center");
    pDisp = new PDisp(pop);
    p.add(pDisp,"West");

    construct(c, p, this, buttonNames, list0);
  }

  public Dimension getPreferredSize() {
    return addBorders(new Dimension(plan.getWidth(),plan.getHeight()));
  }


  public void prepared() {
    if (state == 0) {
      setState(S_START);
      setState(S_RUNNING);
    }
    setView(0,VYM,VXM,0, 8);
  }

  public void stop() {
    thread.cmd.setArgLock(0,thread.DIE);
  }

  /**
     Set the state.
     Repaints the window.
     @param s :     new state (S_xxx)
   */
  private void setState(int s) {

    boolean refresh = (state != s);

    state = s;

    switch (state) {

      case S_START:
        Fmt.setNumberFormat(8,2);
        break;
      case S_RUNNING:
        initPop();
        break;

    }
    if (refresh) {
      updateDisplay();
    }
    repaintButtons();
  }

  private int state;

  public void paintInterior(Graphics g) {
    super.paintInterior(g);

    if (state != S_START) {
      synchronized(thread.cmd)
      {
        Genotype t = pop.getGenotype(0);
        bestEval = eval(t);
        dispE = false;
        displayBest();
      }
    }
  }

  private void displayBest() {

//    Point drawSize = getInteriorSize();
//    System.out.println("drawSize="+drawSize);
//    System.out.println("plan dims="+plan.getWidth()+","+plan.getHeight());
//    final int INSET = 4;
//    double PWIDTH = Math.min((drawSize.x-INSET*2) / (double)plan.getWidth(),
//                          (drawSize.y-INSET*2) / (double)plan.getHeight());
// plan.setScale(PWIDTH);

//    Point origin = centerRect((int)(PWIDTH * plan.getWidth() + INSET),
//                              (int)(PWIDTH * plan.getHeight()) + INSET);

    //setOrigin(origin);

    plan.plot(this);

//    System.out.println("eval, usedArea="+usedArea+", val="+val+", ovflow="+plan.getOverflow());
  }

  /**
   * Enable the buttons according to the state
   */
  public int disabledButtons() {
    int disabled = 0;

    int c = thread.cmd.getArgLock(0);
    if (c == eThread.RUN)
      disabled |= (1 << B_RUN) | (1 << B_STEP);
    else
      disabled |= (1 << B_STOP);

    return disabled;
  }
  public void updateDisplay() {
          repaint();
          pDisp.repaint();
          setPrompt(pop.getStats());
  }

  /**
   * Process a button press.
   * @param b button pressed
   */
  public void processButtonPress(int button) {
    synchronized(thread.cmd) {
    switch (button) {
      case B_STOP:
          thread.cmd.setArgLock(0, thread.STOP);
        break;
      case B_RUN:
        process(false);
        break;
      case B_STEP:
        process(true);
        break;
      case B_RESET:
//          thread.cmd.setArgLock(0, thread.STOP);
        setState(S_RUNNING);
        updateDisplay();
        break;

      case B_SORTED:
      {
        pop.clearGenomes(false);
        /*
        for (int i = 0; i < pop.getPopSize(); i++) {
          Genotype g = pop.getGenotype(i);
          pop.clearGenomes(false);
          for (int j = 0; j < GENOTYPE_LENGTH; j++)
          g.clearBit(j);
        }
*/
        pop.evaluate();
        updateDisplay();
      }
      break;

      case B_SEED:
        thread.cmd.setArgLock(0, thread.STOP);
        initItems(math.rnd(2000));
        pop.clearHistory();
        setState(S_RUNNING);
        updateDisplay();
        break;
    }
    }
  }

  private int weighted(Random r, int range) {
    double v = r.nextDouble() * r.nextDouble();
    return (int)(v * range);
  }

  private void initItems(int seed) {
    Random r = new Random(seed);

    items.clear();
    totalArea = 0;
    CargoItem item = null;
    for (int i = 0; i < ITEMS; i++) {
      double v = r.nextDouble() * r.nextDouble();
      double v2 = r.nextDouble() * r.nextDouble();
      int w = weighted(r,CSIZE_MAX-CSIZE_MIN)+CSIZE_MIN;
      int h = weighted(r,CSIZE_MAX-CSIZE_MIN)+CSIZE_MIN;
      if (h > w) {
        int t = h; h = w; w = t;
      }
      totalArea += w*h;
      item = new CargoItem(w,h,i);
      items.add(item);
    }
    items.sort(this);
  }

  /**
   * Compare two cargo items for DArray: sort.
   *
   * @param a first object
   * @param b second object
   * @return true if b should appear before a in the sorted list.
   */
  public boolean compare(Object a, Object b) {
    CargoItem ca = (CargoItem)a;
    CargoItem cb = (CargoItem)b;
    return (cb.area() > ca.area());
  }

  //private static final double CSCL = Math.sqrt(ITEMS / 100.0);
  private FloorPlan plan = new FloorPlan((int)VXM,(int)(VXM / 5));

//      (int)(1000 * CSCL), (int)(200 * CSCL));

  private DArray items = new DArray();
  private int totalArea;
  protected Population pop;

  private void initPop() {
    if (pop == null) {
      pop = new Population(this,POP_SIZE,
                           OPT_ELITE);
      pop.addTourField(ITEMS);
      pop.addBitField(ITEMS);
      pop.complete();
//      pop.setRestartInterval(80);
      pop.setOperProb(.25,.005);
    }
    pop.init();
  }

  private void process(boolean step) {
    if (state == S_START) {
      setState(S_RUNNING);
      return;
    }

      thread.cmd.setArgLock(0, step ? thread.STEP : thread.RUN);
  }


  private int[] order = new int[ITEMS];
  private boolean[] orientations = new boolean[ITEMS];

  public void extractValues(Genotype g) {
    for (int i = 0; i < ITEMS; i++) {
      order[i] = pop.getSite(g,0,i);
      orientations[i] = pop.getInt(g,1,i,1) != 0;
    }
  }

  public double eval(Genotype g) {
    extractValues(g);
    plan.clear();
    if (order == null) return 0;

    for (int i = 0; i < ITEMS; i++) {
      CargoItem item = (CargoItem)items.get(order[i]);
      item.setRotate(orientations[order[i]]);
      plan.store(item);
    }

    int usedArea = plan.getHeight() * plan.getUsedWidth();
    double val = (plan.getOptimalArea() /
        (double)(usedArea + plan.getOverflow())) * 1000;

if (val > 1000) {
      System.out.println("plan.ht="+plan.getHeight()+", usedW="+plan.getUsedWidth()+
                         ",\n opt area "+plan.getOptimalArea()+", overflow="+plan.getOverflow());
      plan.prob();
}

if (dispE) {
  System.out.println("opt="+Fmt.f(plan.getOptimalArea())+", over="+plan.getOverflow()+" val="
                     +Fmt.f(val)+" usedW="+plan.getUsedWidth());
  System.out.println("ht="+plan.getHeight()+" usedArea="+usedArea);
}
    return val;
  }
private boolean dispE;
private double bestEval;

  public String description(Genotype g) {
    return "CARGOMAP";
  }

  public void evolve() {
    pop.evolve();
  }
}


class eThread implements Runnable {

  public static final int
      RUN = 1
      , STOP = 2
      , STEP = 3
      , DIE = 99
      ;

  public ThreadCommand cmd = new ThreadCommand(2);

  private CargoPanel panel;

  public eThread(CargoPanel panel) {
    this.panel = panel;
    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();

  }

  // Runnable interface
  public void run() {
    int currCmd = 0;
    long prevTicks = System.currentTimeMillis();
    while (currCmd != DIE) {
      boolean update = false;

      synchronized(cmd)
      {
        currCmd = cmd.getArg(0);
         switch (currCmd) {
           case RUN:
             panel.evolve();
             update = (System.currentTimeMillis() - prevTicks > 150);
             break;
            case STEP:
              panel.evolve();
              update = true;
              cmd.setArg(0,0);
              break;
         }
       }
       if (update) {
         prevTicks = System.currentTimeMillis();
         panel.updateDisplay();
       }
       if (currCmd != RUN)
          cmd.sleep(100);
     }
 }
}
