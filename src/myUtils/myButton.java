package myUtils;

import java.awt.*;
import java.awt.event.*;

public class myButton
    extends Button
    implements Globals {
  private char keyEquiv;
  private boolean hidden;

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean h) {
    hidden = h;
  }

  public char getKeyEquiv() {
    return keyEquiv;
  }

  public void setKeyEquiv(char c) {
    keyEquiv = c;
  }

  public myButton(String label) {
    super(label);
  }

  public static void processButtonList(myButton[] buttons, int[] list,
                                       Container c, boolean add) {
    if (list != null)
      for (int i = 0; i < list.length; i++) {
        int bNum = list[i];

        myButton b = buttons[bNum];

        if (b == null)
          continue;
        if (b.isHidden())
          continue;
        if (add)
          c.add(b);
        else
          c.remove(b);
      }
  }

  /**
   * Initialize the buttons for the application.
   * @param c container for buttons
   */
  public static myButton[] initButtons(String[] names,
                                       ActionListener actionListener,
                                       KeyListener keyListener,
                                       int[] addList,
                                       Container c
                                       ) {
    myButton[] buttons = new myButton[names.length];

    for (int i = 0; i < buttons.length; i++) {
      String s = names[i];
      int j = 0, prevJ = 0;
      char key = 0;
      boolean hidden = false;
      boolean ignore = false;
      do {
        prevJ = j;
        char ch = s.charAt(j);
        switch (ch) {
          case '-':
            hidden = true;
            j++;
            break;
          case '.':
            key = s.charAt(j + 1);
            j += 3;
            break;
          case '*':
            if (!DEBUG) {
              ignore = true;
              hidden = true;
            }
            j++;
            break;
        }
      }
      while (prevJ != j);

      String s2 = s.substring(j);
      myButton b = new myButton(s2);
      if (!ignore) {
        b.addActionListener(actionListener);
        b.addKeyListener(keyListener);
        b.setKeyEquiv(key);
      }
      buttons[i] = b;

      b.setHidden(hidden);
    }
    if (addList != null)
      processButtonList(buttons, addList, c, true);
    return buttons;
  }

  private static void unusedaddButtons(myButton[] buttons, int[] list,
                                       Container c) {
    for (int i = 0; i < list.length; i++) {
      int bNum = list[i];
      myButton b = buttons[bNum];
      if (b == null)
        continue;
      if (b.isHidden())
        continue;
      c.add(b);

    }
  }

  public static void enable(Component[] list, int enableFlags) {
    for (int i = 0; i < list.length; i++) {
      Component b = list[i];
      if (b == null)
        continue;
      if (b.isEnabled() != ( (enableFlags & (1 << i)) != 0)) {
        b.setEnabled(!b.isEnabled());
      }
    }

  }

  public static int id(Button b, Button[] bList) {
    int id = -1;
    for (int i = 0; i < bList.length; i++) {
      if (b == bList[i]) {
        id = i;
        break;
      }
    }
    return id;
  }

  public static Button keyButton(char c, myButton[] bList, int[] displayList) {
    Button out = null;
    for (int i = 0; i < displayList.length; i++) {
      myButton b = bList[displayList[i]];
      if (b.isEnabled() && b.getKeyEquiv() == c) {
        out = b;
        break;
      }
    }
    return out;
  }

}