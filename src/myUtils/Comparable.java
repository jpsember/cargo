package myUtils;

public interface Comparable {
  /**
   * Compare two objects for DArray: sort.
   *
   * @param a first object
   * @param b second object
   * @return true if b should appear before a in the sorted list.
   */
  public boolean compare(Object a, Object b);
}