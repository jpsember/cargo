package myUtils;

import java.awt.*;
import java.awt.event.*;

public class TestBed
  extends myPanel
  implements KeyListener, ActionListener, ComponentListener, Globals {

  public void enablePostscript() {
    postScriptEnabled = true;
    System.out.println(
      "postScript output enabled (type '~' to generate .eps file)");
  }

  /**
   * Constructor.
   * Sets title string to "Test Bed", with STYLE_ETCH
   */
  public TestBed(String title) {
    super(title, STYLE_ETCH, null);
    setBackground(Color.white);
    if (DEBUG) {
      tools.warning("*** myUtils DEBUG is true");
    }
  }

  public void repaint() {
    super.repaint(100);
    repaintButtons();
  }

  /**
   * Process a button press.
   *
   * @param button id of button that was pressed
   */
  public void processButtonPress(int button) {
  }

  /**
   * Determine which buttons should be disabled.
   * @return bit #n is SET if button #n is to be disabled
   */
  public int disabledButtons() {
    return 0;
  }

  /**
   * Update the prompt in the text window.
   * @param s new prompt
   */
  public void setPrompt(String s) {
    textPanel.setPrompt(s);
  }

  public void withText(boolean f) {
    withText = f;
  }

 public void setButtonPanelColor(Color c) {
    buttonPanelColor = c;
  }

  /**
   * Add TestBed window to component with associated prompt and button
   * windows; add various listeners, and a list of buttons
   *
   * @param c Container to hold the various windows
   * @param tbContainer component that contains the TestBed
   * @param tb TestBed component
   * @param buttonNames button strings
   * @param buttonList button ids
   */
  public void construct(Container c, Container tbContainer, TestBed tb,
                        String[] buttonNames, int[] buttonList) {

    // Create a panel to hold the test bed window, the prompt window,
    // and button window.

    GridBagLayout gb = new GridBagLayout();
    Panel outerPanel = new Panel(gb);
    c.add(outerPanel);

    if (withText) {
      textPanel = new TextPanel(null, 1);
      textPanel.addKeyListener(tb);
    }
    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.BOTH;

    tb.addComponentListener(this);

    Panel buttonWindow = new myPanel(null, STYLE_ETCH, buttonPanelColor);

    initButtons(buttonNames, buttonList, buttonWindow);
    tb.addMouseListener(tb);
    tb.addMouseMotionListener(tb);

    // Add key listeners to various panels.
    outerPanel.addKeyListener(tb);

    tbContainer.addKeyListener(tb);
    if (tbContainer != tb) {
      Component[] cs = tbContainer.getComponents();
      for (int i = 0; i < cs.length; i++) {
        cs[i].addKeyListener(tb);
      }
    }

    buttonWindow.addKeyListener(tb);

    outerPanel.add(tbContainer, setGBC(gc, 0, 0, 1, 1, 100, 1185));
    {
      if (withText) {
        outerPanel.add(textPanel, setGBC(gc, 0, 1, 1, 1, 100, 15));
      }
      outerPanel.add(buttonWindow, setGBC(gc, 0, 2, 1, 1, 100, 10));
    }
  }

  /**
   * Add TestBed window to component with associated prompt and button
   * windows; add various listeners, and a list of buttons
   *
   * @param c Container to hold the various windows
   * @param tb TestBed to add
   * @param buttonNames button strings
   * @param buttonList button ids
   */
  public void construct(Container c, TestBed tb,
                        String[] buttonNames, int[] buttonList) {
    construct(c, tb, tb, buttonNames, buttonList);
  }


  /**
   * Called when size of window has changed.  Will not be called
   * until the drawArea is known.  The applet's initialization
   * code can be tied to this method.
   */
  public void prepared() {
  }

  public void componentResized(ComponentEvent e) {
    super.componentResized(e);
    prepared();
  }

  // --- MouseListener interface---------------
  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  // --- end of MouseListener interface--------

  // --- MouseMotionListener interface---------
  public void mouseMoved(MouseEvent e) {
    lastMouseEvent = e;
  }

  public void mouseDragged(MouseEvent e) {
    lastMouseEvent = e;
  }

  // ------- end of interface -----------------

  /**
   * Update button enabling, repaint if necessary
   */
  public void repaintButtons() {
    if (buttons == null) {
      return;
    }
    int disabled = disabledButtons();

    myButton.enable(buttons, ~disabled);
  }

  /**
   * Initialize the buttons for the application.
   * @param c container for buttons
   */
  private void initButtons(String[] buttonNames, int[] buttonList, Container c) {

    buttonContainer = c;
    buttons = myButton.initButtons(
      buttonNames, this, this, buttonList, buttonContainer);
    theButtonList = buttonList;
    repaintButtons();
  }

  public void replaceButtons(int[] buttonList) {
    myButton.processButtonList(buttons, theButtonList, buttonContainer, false);
    theButtonList = buttonList;
    myButton.processButtonList(buttons, theButtonList, buttonContainer, true);
    buttonContainer.validate();

    // Have main panel request focus in case some old button had it
    this.requestFocus();

  }

  public myButton button(int id) {
    return buttons[id];
  }

  /**
   * Find a button with a keyboard equivalent matching a character.
   * If found, process button press.
   * @param c character to search for in button list
   * @return true if this key was processed
   */
  private boolean procButtonKey(char c) {
    boolean processed = true;
    do {
    if (postScriptEnabled) {
      if (c == 126) {
        // Generate a postscript version of the main panel
        System.out.println("Generating postscript version of panel");
        this.postScript(null);
        break;
      }
    }

    Button b = myButton.keyButton(c, buttons, theButtonList);
    if (b != null) {
      processButtonPress(b);
      break;
    }
    processed = false;
    } while  (false);
    return processed;
  }

  /**
   * Process a button press.
   * @param b button pressed
   */
  private void processButtonPress(Button b) {

    // Find index of button
    int button = myButton.id(b, buttons);

    processButtonPress(button);
    // Re-enable buttons in case state has changed.
    repaintButtons();
  }

// --- KeyListener interface ----------------

  /**
   * Determine if key event is a mouse-button equivalent (f1 or f2)
   * @param e : key event
   * @return 0 if f1, 1 if f2, or -1 if neither
   */
  private static int functionKey(KeyEvent e) {
    int out = -1;
    int code = e.getKeyCode();
    switch (code) {
      case KeyEvent.VK_F1:
        out = 0;
        break;
      case KeyEvent.VK_F2:
        out = 1;
        break;
    }
    return out;
  }

  public void keyPressed(KeyEvent e) {
  }


  /**
   * Get the last mouse event generated by a move or drag event.
   * @return mouse event, null if none exists
   */
  protected MouseEvent lastMouseEvent() {
    return lastMouseEvent;
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
    if (!procButtonKey(Character.toUpperCase(e.getKeyChar()))) {
      processKeyTyped(e);
    }
  }

  /**
   * Process a typed key that didn't match a button equivalent.
   * @param e : key event of typed key
   */
  public void processKeyTyped(KeyEvent e) {}

  // --- ActionListener interface ----------------
  public void actionPerformed(ActionEvent e) {
    processButtonPress( (Button) e.getSource());
  }

// ---------------------------------------------------------

  // if true, a text panel is constructed as well
  private boolean withText = true;
  // text panel associated with the TestBed
  private TextPanel textPanel;

  private Container buttonContainer;
  private Color buttonPanelColor = new Color(50, 200, 100);

  // Button objects associated with each command
  private myButton[] buttons;

  private Dimension drawArea = new Dimension();

  // last mouse event for move or drag, for use by keyboard equivalent
  // handlers that need a mouse event as a parameter
  private MouseEvent lastMouseEvent;

  // true if PostScript output is enabled
  private boolean postScriptEnabled = false;

  private int[] theButtonList;
}
