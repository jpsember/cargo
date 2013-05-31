package myUtils;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class FrameApplet extends Applet implements WindowListener {
  private boolean fApp = false;
  private boolean fInFrame = false;
  /**
   * Frame containing program; if null, not running in frame
   */
  protected Frame frame;

  // WindowListener interface
  public void windowClosing(WindowEvent e) {
        frame.dispose();
        frame = null;
        if (fApp) System.exit(0);
  }
  public void windowOpened(WindowEvent e){}
  public void windowClosed(WindowEvent e){}
  public void windowIconified(WindowEvent e){}
  public void windowDeiconified(WindowEvent e){}
  public void windowActivated(WindowEvent e){}
  public void windowDeactivated(WindowEvent e){}

  private void openFrame() {
    // Create a frame for the applet to pop up from.
    frame = new Frame();
//    frame.setTitle(title);
    Dimension d = getPreferredSize();
    frame.setBounds(20,20,d.width,d.height);
//    frame.setBounds(20,20,640,480);
    frame.addWindowListener(this);
  }
  public Dimension getPreferredSize() {
    return new Dimension(800,600);
  }

  public void init() {
    if (fApp) {
      fInFrame = true;
      openFrame();
      return;
    }

    Dimension ps = this.getPreferredSize();

    // Decide whether to run in the applet bounds,
    // or as a resizable frame.

    fInFrame = (parseAppletParameter("WINDOW",1) > 0);
    Container c = this;
    if (fInFrame) {
      if (frame == null)
        openFrame();
//      else
//        frame.show();
      c = frame;
    }

    c.setLayout(new BorderLayout());
    openComponents(c);
    c.show();
  }


  public int parseAppletParameter(String name, int defaultValue) {
    int value = defaultValue;
    String param = getParameter(name);
    if (param != null) {
      try {
        value = Integer.parseInt(param);
      } catch (NumberFormatException e) {
        System.out.println("Applet parameter: "+name+" has illegal value");
      }
    }
    return value;
  }

  /**
   * Override this method to add the components to the
   * frame.
   */
  public void openComponents(Container c) {
    System.out.println("*** No components added");
//    new OrthoConvexPanel(c);
  }

}

