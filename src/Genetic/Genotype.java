package Genetic;

import myUtils.*;
import java.util.*;

public class Genotype {

  public Genotype(Population p) {
    pop = p;

    str = new BitSet(p.getGenomeLength());
//    this.strLen = strLen;
 //   str = new BitSet(strLen);
  }

  /* Construct a genotype as a copy of another. */
  public Genotype(Genotype src) {
    pop = src.pop;
    evaluation = src.evaluation;
    str = (BitSet) src.str.clone();
//    strLen = src.strLen;
  }

  public void randomize() {
    Random r = pop.getRandom();

    for (int i = str.size() - 1; i >= 0; i--) {
      if (r.nextInt() < 0)
        str.clear(i);
      else
        str.set(i);
    }
    evalNecessary = true;
  }

  public String toString() {

    StringBuffer sb = new StringBuffer();

    sb.append('[');
    for (int i = 0; i < pop.fieldCount(); i++) {
      if (pop.fieldCount() > 1)
        sb.append(" f"+i+":");

      PopField f = pop.getField(i);

      if (!f.tour) {
        int size = f.stringSize;
        if (size > 50)
          size = 8;
        for (int k = 0; k < size; k++) {
          sb.append(str.get(k+f.offset) ? '1' : '.');
        }
      } else {
        int size = f.tourSites;
        if (size > 20)
          size = 8;
          for (int k = 0; k < size; k++)
            sb.append(Fmt.f(pop.getSite(this,i,k),4));
      }

    }
    sb.append(']');
    if (evalNecessary) {
      sb.append("*** Eval required ***");
    }
    else {
      sb.append(" Value:");
      sb.append(Fmt.f(evaluation));
    }

    return sb.toString();
  }
/*
  public int getLength() {
    return strLen;
  }
*/
  /* Read an unsigned integer from the bit string.
     Preconditions:
      startBit = index of first (most significant) bit in the integer
      length = the number of bits to read
     Postconditions:
      returns a value from 0...(2^length)-1
   */
  public int getInt(int startBit, int length) {
    int val = 0;
    while (length > 0) {
      val = (val << 1) | (str.get(startBit++) ? 1 : 0);
      length--;
    }
    return val;
  }

  /* Write an unsigned integer to the bit string.
   */
  public void putInt(int startBit, int length, int value) {
    int bit = 1 << (length - 1);
    while (length > 0) {
      str.clear(startBit);
      if ((value & bit) != 0)
          str.set(startBit);
      bit >>= 1;
      startBit++;
      length--;
    }
  }

  public boolean getBit(int i) {
    return str.get(i);
  }

  public void storeBit(int i, boolean f) {
    if (f)
      str.set(i);
    else
      str.clear(i);
    evalNecessary = true;
  }

  public void setBit(int i) {
    str.set(i);
    evalNecessary = true;
  }

  public void clearBit(int i) {
    str.clear(i);
    evalNecessary = true;
  }

  public void setValue(double val) {
    evaluation = val;
    evalNecessary = false;
  }

  public double getValue() {
    tools.ASSERT(!evalNecessary);
    return evaluation;
  }

  public boolean needsEvaluation() {
    return evalNecessary;
  }

  private static void pr(String s) {
    System.out.println(s);
  }

  private BitSet str;
//  private int strLen;
  private Population pop;
  private double evaluation;
  private boolean evalNecessary;

}