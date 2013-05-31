package myUtils;

public class DStack extends DArray {
  public DStack() {}
  public void push(int i) {
    add(new Integer(i));
  }
  public void push(Object obj) {
    add(obj);
  }
  public int peekInt(int dist) {
    return ((Integer)peek(dist)).intValue();
 }

 public int peekInt() {
   return peekInt(0);
 }
 public Object peek(int dist) {
   return get(size() + dist - 1);
 }
 public Object peek() {return peek(0);}

  public Object pop() {
    int top = size() -1 ;
    Object obj = get(top);
    remove(top);
    return obj;
  }
  public int popInt() {
    return ((Integer)pop()).intValue();
  }
}