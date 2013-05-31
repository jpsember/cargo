package myUtils;

public class DQueue /*extends DArray*/ {
  public DQueue() {
   // tools.warning("Queues are not optimized for deletion");
    }


  public void push(int i) {
    add(new Integer(i));
  }
  public void push(Object obj) {
    add(obj);
  }

 public Object peek() {
   return first.data;
 }
public boolean isEmpty() {
  return size == 0;
}

  public Object pop() {
    Object obj = first.data;
    if (first == last) {
      first = null;
      last = null;
    } else {
      first = first.next;
    }
    size--;
    return obj;
  }

  public int popInt() {
    return ((Integer)pop()).intValue();
  }

  private QueueEntry first, last;
  private int size;

  private void add(Object obj) {
    QueueEntry ent = new QueueEntry(obj);
    if (isEmpty()) {
      first = ent;
      last = ent;
    } else {
      last.next = ent;
      last = ent;
    }
    size++;
  }

  public int size() {
    return size;
  }
}

class QueueEntry {
  public QueueEntry(Object data) {
    this.data = data;
  }
  QueueEntry next;
  Object data;
}
