package Genetic;
import myUtils.*;
import java.util.*;

class PopField implements GenGlobals {
  public int stringSize;
  public boolean tour;
  public int tourWordSize;
  public int tourSites;
  public int offset;

  private PopField(int strSize) {
    stringSize = strSize;
  }

  public static PopField tourField(int tourSites, int offset) {
    int wSize = calcTourWordSize(tourSites);
    PopField f = new PopField(wSize * tourSites);
    f.tourWordSize = wSize;
    f.tourSites = tourSites;
    f.tour = true;
    f.offset = offset;
    return f;
  }

  public static PopField bitField(int size, int offset) {
    PopField f = new PopField(size);
    f.offset = offset;
    return f;
  }

  public static int calcTourWordSize(int tourSites) {
    int wSize = 0;
    while ( (1 << wSize) < tourSites)
      wSize++;
    return wSize + 1;
  }

}
