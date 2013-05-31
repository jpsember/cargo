package myUtils;
//import java.util.ArrayList;
public class DArray {

    public DArray() {
    }
    private void swap(int i, int j) {
      Object iObj = n[i];
     n[i] = n[j];
    n[j] = iObj;
    }

    public void addInt(int val) {
      add(new Integer(val));
    }

    public void addInt(int position, int val) {
      add(position, new Integer(val));
    }

    public int getInt(int item) {
      return ((Integer)(get(item))).intValue();
    }

    public void sort(Comparable c) {
      for (int i = 1; i < size; i++) {
        for (int j = i-1; j>=0; j--) {
          if (c.compare(get(j),get(j+1)))
            swap(j,j+1);
        }
      }
    }

    /* Construct an array as a copy of another one.
     */
    public DArray(DArray src) {
        size = src.size;
        if (src.n != null) {
          n = new Object[src.size];
          copyElements(src.n,0,n,0,size);
        }
    }

    /* Insert a value into the array.
       It is added to the end of the existing items.

       Preconditions:
        value : the value to insert
     */
    public void add(Object obj) {
      add(size,obj);
    }

    public boolean isEmpty() {
      return size == 0;
    }

    public void clear() {
      for (int i = 0; i < size; i++) {
        n[i] = null;
      }
      size = 0;
    }

    /* Insert a value at a particular position in
       the array, shifting following items higher

       Preconditions:
        value    : the value to add
        position : the position to insert the value at;
                    0 <= position <= size()
     */
    public void add(int position, Object obj) {

        ensureCapacity(size+1);
        copyElements(this,position,this,position+1,size-position);
        size++;
        set(position,obj);
    }

    public void remove(int ind) {
      remove(ind,1);
    }

    public void remove(int ind, int amt) {
      int end = Math.min(ind+amt,size);
      if (end >= ind) {
        amt = end - ind;
	      copyElements(n,ind+amt,n,ind,size-ind-amt);
  	    size-= amt;
      }
    }

    /* Return the value at an index

       Preconditions:
        index : an index into the array, 0...size-1

       Postconditions:
        Returns the value stored at that index.
    */
    public Object get(int index) {
     // tools.ASSERT(index < size);
      return n[index];
    }

    public void set(int position, Object obj) {
    //  tools.ASSERT(position < size);
      n[position] = obj;
    }

    public int size() {
        return size;
    }

    public void ensureCapacity(int newCapacity) {
//      public void ensureCapacity() {
//    ensureCapacity(size + 1);
//}
        int currentLength = (n == null ? 0 : n.length);

        if (newCapacity > currentLength) {
            int newSize = newCapacity * 2;

            // If size is quite small at present, impose
            // a minimum size to avoid having to resize
            // repeatedly for small values.

            final int MIN_SIZE = 8;
            newSize = Math.max(newSize, MIN_SIZE);

            Object[] nNew = new Object[newSize];
            copyElements(n,0,nNew,0,size);
            n = nNew;
        }
    }

    /* Copy a range of values from one Array to another.
    */
    public static void copyElements(DArray src, int srcInd,
     DArray dest, int destInd, int length) {

        copyElements(src.n,srcInd,dest.n,destInd,length);
    }

    /* Copy a range of integers from one int array to another.
    */
    public static void copyElements(Object[] src, int srcInd,
     Object[] dest, int destInd, int length) {

        if (srcInd >= destInd) {
            while (length-- > 0)
                dest[destInd++] = src[srcInd++];
        } else {
            destInd += length;
            srcInd += length;
            while (length-- > 0)
                dest[--destInd] = src[--srcInd];
        }
    }

    private Object[] n;
    private int size = 0;

    /**
     * Construct an array from a list.
     * @return an array of Object
     */
     public Object[] toArray() {
       Object[] a = new Object[size()];
       for (int i = 0 ; i < size(); i++)
       a[i] = get(i);
       return a;
     }

  }
