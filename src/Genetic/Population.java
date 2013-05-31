package Genetic;

import myUtils.*;
import java.util.*;

/*
    Genetic Population class
 */
public class Population
    implements myUtils.Comparable, GenGlobals, IGenEvaluator {

  // Maximum number of bit or tour fields in genome
  private static final int MAX_FIELDS = 4;
  // Field records
  private PopField[] fields = new PopField[MAX_FIELDS];
  // Number of fields
  private int fieldCount;
  // true if genome definition is complete
  private boolean defined;

  /**
   * Constructor.
   *
   * @param eval evaluation function
   * @param popSize size of population
   * @param flags miscellaneous flags (OPT_xxx)
   */
  public Population(IGenEvaluator eval, int popSize, int flags) {
    if (eval == null)
      eval = this;
    this.flags = flags;
    this.evaluator = eval;
    this.popSize = popSize;
    setOperProb(.25, .01);
  }

  /**
   * Add a field of bits to the genome.
   * @param stringLength length of string of bits
   */
  public void addBitField(int stringLength) {
    addField(PopField.bitField(stringLength, genomeLen));
  }

  /**
   * Add a field of a site tour to the genome.
   * @param tourSites number of sites in tour
   */
  public void addTourField(int tourSites) {
    addField(PopField.tourField(tourSites, genomeLen));
  }

  /**
   * Specify that the genome's definition has been completed.
   * No further fields can be added after this point.
   */
  public void complete() {
    tools.ASSERT(!defined);
    defined = true;
    init();
  }

  private void addField(PopField f) {
    tools.ASSERT(fieldCount < MAX_FIELDS);
    tools.ASSERT(!defined);
    fields[fieldCount++] = f;
    genomeLen += f.stringSize;
  }

  public void clearHistory() {
    bestGenotype = null;
    eliteGenotype = null;
    elite.clear();
  }

  public void setOperProb(double crossover, double mutate) {
    cvProb = crossover;
    mutateProb = mutate;
  }

  public void setRestartInterval(int interval) {
    restartInterval = interval;
  }

  /**
   * Initialize the population to new, random genotypes
   */
  public void init() {
    tools.ASSERT(defined);
    generation = 0;
    random = new Random();

    pop = new DArray();
    children = new DArray();

    for (int i = 0; i < popSize; i++) {
      pop.add(new Genotype(this));
    }
    clearGenomes(true);

  }

  private Genotype g(int index) {
    return (Genotype) pop.get(index);
  }

  private int[] order;

  private void initGenome(Genotype g, boolean rnd) {

    for (int i = 0; i < fieldCount; i++) {
      PopField f = fields[i];
      if (!f.tour) {
        // Initialize bitstring field.
        for (int j = 0; j < f.stringSize; j++) {
          g.storeBit(j + f.offset, rnd ? tools.rndBool(random) : false);
        }
      }
      else {
        // Initialize tour field.

        if (order == null || order.length < f.tourSites)
          order = new int[f.tourSites];
        for (int j = 0; j < f.tourSites; j++)
          order[j] = j;
        if (rnd) {
          for (int j = 0; j < f.tourSites; j++) {
            int k = tools.rnd(random,(f.tourSites - j));
            int temp = order[k];
            order[k] = order[j];
            order[j] = temp;
          }
        }
        for (int j = 0; j < f.tourSites; j++)
          putTourInt(g, f, j, order[j]);
      }
    }
  }

  public int getSite(Genotype g, int field, int index) {
    return getTourInt(g, fields[field], index);
  }

  public int getInt(Genotype g, int field, int index, int length) {
    return g.getInt(index + fields[field].offset, length);
  }

  private void putTourInt(Genotype g, PopField f, int index, int value) {
    g.putInt(f.offset + f.tourWordSize * index, f.tourWordSize, value);
  }

  private int getTourInt(Genotype g, PopField f, int index) {
    return g.getInt(f.offset + f.tourWordSize * index, f.tourWordSize);
  }

  /**
   * Clear the genomes to initial values
   * @param random true to randomize the genome
   */
  public void clearGenomes(boolean random) {
    {
      for (int i = 0; i < getPopSize(); i++) {
        Genotype g = getGenotype(i);
        initGenome(g, random);
      }
      evaluate();
    }
  }

  /**
   * Compare two objects for DArray: sort.
   *
   * @param a first object
   * @param b second object
   * @return true if b should appear before a in the sorted list.
   */
  public boolean compare(Object a, Object b) {
    Genotype ga = (Genotype) a, gb = (Genotype) b;
    if (flag(OPT_MINIMIZE))
      return (gb.getValue() < ga.getValue());

    return (gb.getValue() > ga.getValue());
  }

  public boolean flag(int f) {
    return (flags & f) != 0;
  }

//  private int tourSize;
//  private int tourStartBit;
//  private int tourWordSize;

  private void addChild(Genotype c) {
    if (children.size() < popSize) {
      children.add(c);
    }
  }

  private static final boolean d = false;

  /**
   * Prepare parameters for weighted roulette wheel.
   * We examine the min/max fitness values of the population.
   */
  private void prepareWheel() {
    double effMin = 0;
    double range = 0;

    if (!flag(OPT_MINIMIZE)) {
      effMin = meanValue - stdDeviation * 2;
      range = maxValue - effMin;
      wheelBaseValue = effMin;
      if (range < 0.001)
        wheelBaseValue = effMin - 20.0;
    }
    else {
      effMin = (meanValue + stdDeviation * 2);
      range = effMin - minValue;
      wheelBaseValue = effMin;
      if (range < 0.001)
        wheelBaseValue = effMin + 20.0;
    }

    wheelTotalValue = 0;
    for (int i = 0; i < popSize; i++) {
      double v = g(i).getValue();
      if (!flag(OPT_MINIMIZE)) {
        if (v >= wheelBaseValue)
          wheelTotalValue += (v - wheelBaseValue);
      }
      else {
        if (v <= wheelBaseValue)
          wheelTotalValue += (wheelBaseValue - v);
      }

    }
//    wheelTotalValue = totalValue - (wheelBaseValue * popSize);

    if (d) {
      System.out.println("totalValue=" + totalValue + ", wheelBase=" +
                         wheelBaseValue + ", wheelTotal=" + wheelTotalValue);
      int i;
      double d = 0;
      for (i = 0; i < popSize; i++) {
        double v = g(i).getValue();
        d += (v - wheelBaseValue);
        System.out.println("i=" + i + ", d=" + Fmt.f(d));
      }
      System.out.println("wheelTotalValue=" + Fmt.f(wheelTotalValue));
    }
  }

  /**
   * Perform a breed / evaluate / cull cycle (one generation).
   */
  public void evolve() {
    if (d)
      System.out.println("evolve, cv=" + cvProb);

    prepareWheel();

    // Construct n children from the current parents.
    if (flag(OPT_ELITE) && eliteGenotype != null)
      addChild(eliteGenotype);

    while (children.size() < popSize) {

      // Decide whether to
      //  a) reproduce (i.e. clone) a parent
      //  b) use crossover to produce two children from two parents

      if (random.nextDouble() < cvProb)
        performCrossover();
      else
        cloneParent();
    }

    DArray.copyElements(children, 0, pop, 0, popSize);
    children.clear();

    for (int i = 0; i < popSize; i++) {
      if (i == 0 && flag(OPT_ELITE) && eliteGenotype != null)
        continue;

      mutate(g(i));
    }

    // Evaluate the population.
    evaluate();
    generation++;

    if (restartInterval > 0 && generation % restartInterval == 0) {
      performRestart();
    }
  }

  private static final int ELITE_SIZE = 10;

  private void performRestart() {
    elite.add(new Genotype(g(0)));
    if (elite.size() >= ELITE_SIZE) {
      elite.sort(this);
      if (elite.size() > ELITE_SIZE)
        elite.remove(ELITE_SIZE);
    }

    // Randomize the population.
    for (int i = 0; i < popSize; i++)
      initGenome(g(i),true);

      // If we have a good selection of elite candidates,
      // seed the population with them initially.
    if (elite.size() >= 10) {
      pop.remove(pop.size() - 10, 10);

      for (int i = 0; i < 10; i++) {
        pop.add(new Genotype( (Genotype) elite.get(i)));
      }
    }
    evaluate();
  }

  private void cloneParent() {
    int parent = chooseWeightedGenotype();
    if (d)
      System.out.println(" cloning parent #" + parent);
    addChild(new Genotype(g(parent)));
  }

  /**
   * Get the mean fitness value
   * @return mean fitness value
   */
  public double getMean() {
    return meanValue;
  }

  /**
   * Get the standard deviation of the fitness values
   * @return standard deviation
   */
  public double getStdDeviation() {
    return stdDeviation;
  }

  /**
   *  Return a string describing the genotype.
   * @return string describing a genotype
   */
  public String getDescription(Genotype t) {
    return evaluator.description(t);
  }

  /**
   * Display the population to System.out.
   */
  public void display() {
    for (int i = 0; i < pop.size(); i++) {
      System.out.println("# " + Fmt.f(i) + ": " + g(i));
    }
    System.out.println(getStats());
  }

  /**
   * Get the best historical score.
   * @return best historical score
   */
  public double getBestScore() {
    return bestHistorical;
  }

  /**
   * Get the best historical genotype
   * @return best individual
   */
  public Genotype getBest() {
    return bestGenotype;
  }

  /**
   * Construct a string describing the state of the population
   * @return description
   */
  public String getStats() {
    return ("Gen " + Fmt.f(generation, 5)
            + " mean=" + Fmt.f(meanValue)
            + (flag(OPT_MINIMIZE) ? " min" : " max") + ", curr=" +
            Fmt.f(bestValue)
            + " ever=" + Fmt.f(bestHistorical)
            + " std dev=" + Fmt.f(stdDeviation)
            );
  }

  /**
   * Construct a string describing the population
   * @return description
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Population");
    sb.append(" stringLength=");
    sb.append(genomeLen);
    sb.append(getStats());
    return sb.toString();
  }

  /**
   * Get a particular individual from the population
   * @param index index of individual (0...n-1)
   * @return individual
   */
  public Genotype getGenotype(int index) {
    return (Genotype) pop.get(index);
  }

  /**
   * Get the random number generator associated with this population
   * @return
   */
  public Random getRandom() {
    return random;
  }

  /* Choose a parent genotype that is weighted to favor those
     with good evaluations.
   */
  private int chooseWeightedGenotype() {
    double rVal = random.nextDouble() * wheelTotalValue;
    if (d)
      System.out.println("chooseWeightedG, rVal=" + Fmt.f(rVal));
    int i;
    for (i = 0; i < popSize - 1; i++) {
      double v = g(i).getValue();
      if (!flag(OPT_MINIMIZE)) {
        if (v < wheelBaseValue)
          continue;
        rVal -= (v - wheelBaseValue);
      }
      else {
        if (v > wheelBaseValue)
          continue;
        rVal -= (wheelBaseValue - v);
      }

      if (rVal < 0.0) {
        if (d)
          System.out.println(" rVal " + Fmt.f(rVal) + ", parent " + i);
        break;
      }
    }
    /*    if (i == popSize-1)
          tools.ASSERT(g(i).getValue()-wheelBaseValue >= rVal);
     */
    return i;
  }

  /* Mutate a genotype by randomly fiddling its bits.
   */
  private void mutate(Genotype t) {

    for (int j = 0; j < fieldCount; j++) {
      PopField f = fields[j];
      if (!f.tour) {

        // Mutate bitfield.
        for (int i = f.stringSize + f.offset - 1; i >= f.offset; i--) {
          if (random.nextDouble() < mutateProb) {
            if (t.getBit(i))
              t.clearBit(i);
            else
              t.setBit(i);
          }
        }
      }
      else {
        mutateTour(t, f);

      }
    }
  }

  private void mutateTour(Genotype t, PopField f) {
    for (int i = 0; i < f.tourSites; i++) {
    if (random.nextDouble() >= mutateProb) continue;

    // Mutate tour.
    switch (tools.rnd(random,3)) {
      case 0: {

        // Choose two sites and exchange them.
        int s0 = tools.rnd(random,f.tourSites),
            s1 = tools.rnd(random,f.tourSites);
        int v0 = getTourInt(t, f, s0),
            v1 = getTourInt(t, f, s1);
        putTourInt(t, f, s0, v1);
        putTourInt(t, f, s1, v0);
      }
      break;
      case 1: {
        // Invert a sequence of sites.
        int s0 = tools.rnd(random,f.tourSites),
            s1 = tools.rnd(random,f.tourSites);
        int a = Math.min(s0,s1);
        int b = Math.max(s0,s1);
        int c = (a+b)/2;
        while (a < c) {
          int v0 = getTourInt(t, f, a),
              v1 = getTourInt(t, f, b);
          putTourInt(t, f, b, v0);
          putTourInt(t, f, a, v1);
          a++;
          b--;
        }
      } break;
      case 2: {
        // Displacement:  select a subtour and insert it in a random place.
        int s0 = tools.rnd(random,f.tourSites),
            s1 = tools.rnd(random,f.tourSites);
        int a = Math.min(s0,s1);
        int b = Math.max(s0,s1);
        int len = b+1-a;
        if (util == null || util.length < f.tourSites*2)
          util = new int[f.tourSites*2];
          int d2 = f.tourSites;
        for (int k = 0; k < f.tourSites; k++) {
          int h = getTourInt(t,f,k);
          if (k >= a && k <= b)
            util[k-a] = h;
          else
            util[(d2++)] = h;
        }
        int dest = tools.rnd(random,f.tourSites + 1 - len);
        d2 = f.tourSites;
        int k = 0;
        while (k < dest) {
          putTourInt(t,f,k,util[d2++]);
          k++;
        }
        while (k < dest + len) {
          putTourInt(t,f,k,util[k - dest]);
          k++;
        }
        while (k < f.tourSites) {
          putTourInt(t,f,k,util[d2++]);
          k++;
        }
      } break;
    }
    }
  }
  private int[] util;

  /**
   * Evaluate every genotype in the population, and calculate
   * some stats regarding the entire population (standard deviation,
   * etc.)
   */
  public void evaluate() {
    if (d)
      System.out.println("evaluating population");
    totalValue = 0;
    double varCalc = 0;
    int totalPop = 0;

    minValue = maxValue = 0;

    boolean bestDefined = false;
    for (int i = 0; i < popSize; i++) {
      totalPop++;
      double val;

      Genotype g = g(i);
      if (true || g.needsEvaluation()) {
        val = evaluator.eval(g);
        g.setValue(val);
        if (val < minValue)
          minValue = val;
        if (val > maxValue)
          maxValue = val;

        if (d)
          System.out.println(" " + i + ": value " + val);
      }
      else
        val = g.getValue();

      if (!bestDefined || (!flag(OPT_MINIMIZE) && bestValue < val) ||
          (flag(OPT_MINIMIZE) && bestValue > val)) {
        bestDefined = true;
        bestValue = val;
        eliteGenotype = g;

        if (bestGenotype == null ||
            (!flag(OPT_MINIMIZE) && bestHistorical < bestValue)
            || (flag(OPT_MINIMIZE) && bestHistorical > bestValue)) {
          bestHistorical = bestValue;
          bestGenotype = new Genotype(g);
        }
      }
      totalValue += val;
      varCalc += val * val;
    }
    meanValue = totalValue / totalPop;
    double variance = varCalc / totalPop - meanValue * meanValue;
    stdDeviation = Math.sqrt(variance);
    pop.sort(this);
    if (d)
      System.out.println(" sorted, mean=" + meanValue);
  }

  public int getPopSize() {
    return popSize;
  }

  public int getGenomeLength() {
    return genomeLen;
  }

  public int getGeneration() {
    return generation;
  }

  private void performCrossover() {

    // Choose a field to perform the crossover with.
    // This should be handled differently later.

    int field = math.rnd(fieldCount);
    PopField f = fields[field];

    int parent = chooseWeightedGenotype();
    int parent2 = chooseWeightedGenotype();

    if (!f.tour) {

      // Choose a crossover point.
      int cvPoint = tools.rnd(random,f.stringSize);
      int cvPoint2 = tools.rnd(random,f.stringSize);

      int cStart = Math.min(cvPoint, cvPoint2);
      int cLength = Math.max(cvPoint, cvPoint2) + 1 - cStart;

      if (d)
        System.out.println(" crossover parents " + parent + "," + parent2 +
                           ", bits " +
                           cStart + ".." + (cStart + cLength - 1));

      for (int pass = 0; pass < 2; pass++) {
        Genotype g1 = g(parent), g2 = g(parent2);
        Genotype t = new Genotype(g1);
        for (int i = cStart; i < cStart + cLength; i++) {
          t.storeBit(i + f.offset, g2.getBit(i + f.offset));
        }
        addChild(t);
        int temp = parent;
        parent = parent2;
        parent2 = temp;
      }
    }
    else {
      tourCrossover(f, parent, parent2);
    }
  }

  private char[] cvFlags;

  private void tourCrossover(PopField f, int p1, int p2) {
    final boolean d = false;

    // Choose a crossover point.
    int cvPoint = tools.rnd(random,f.tourSites);
    int cvPoint2 = tools.rnd(random,f.tourSites);

    int cStart = Math.min(cvPoint, cvPoint2);
    int cLength = Math.max(cvPoint, cvPoint2) + 1 - cStart;

    if (d)
      System.out.println(" crossover parents " + p1 + "," + p2 +
                         ", sites " +
                         cStart + ".." + (cStart + cLength - 1));

    for (int pass = 0; pass < 2; pass++) {
      Genotype g1 = g(p1), g2 = g(p2);
      Genotype t = new Genotype(g1);
      if (d)
        System.out.println("g1=" + g1 + "\n" + "g2=" + g2);

      if (cvFlags == null || cvFlags.length < f.tourSites)
        cvFlags = new char[f.tourSites];

      for (int i = f.tourSites - 1; i >= 0; i--)
        cvFlags[i] = 0;

      int src = cStart;
      for (int i = 0; i < cLength; i++) {
        int k = getTourInt(g2, f, src);
        cvFlags[k] = 1;
        putTourInt(t, f, src, k);
        src++;
      }
      if (src == f.tourSites)
        src = 0;
      int dest = src;
      while (dest != cStart) {
        int k = getTourInt(g1, f, src);
        if (cvFlags[k] == 0) {
          putTourInt(t, f, dest, k);
          cvFlags[k] = 1;
          if (++dest == f.tourSites)
            dest = 0;
        }
        if (++src == f.tourSites)
          src = 0;
      }
      if (d)
        System.out.println(" t=" + t);

      addChild(t);
      int temp = p1;
      p1 = p2;
      p2 = temp;
    }
  }

  // Length of genome in bits
  private int genomeLen;
  // object to perform evaluations
  private IGenEvaluator evaluator;
  // generation counter
  private int generation;
  // the individuals
  private DArray pop, children;

  // Random number generator
  private Random random;

  // Probability of mutation, per bit
  private double mutateProb;
  // Probability of crossover while breeding
  private double cvProb;

  // The total value of the population
  private double totalValue;
  // Mean value of genotype
  private double meanValue;
  private double minValue, maxValue;
  private double wheelBaseValue, wheelTotalValue;
  private double stdDeviation;
  private double bestValue;
  private double bestHistorical;
  private Genotype bestGenotype;
  // # individuals in population
  private int popSize;
  // private boolean withElite;
  private Genotype eliteGenotype;
  private int restartInterval;
  private int flags;
  private DArray elite = new DArray();

  /**
   * Constructor
   *
   * @param stringLength length of genome, in bits
   * @param eval evaluator object
   * @param popSize number of individuals in population
   */
  /*
      public Population(int stringLength, int tourSize, IGenEvaluator eval,
                      int popSize, int flags) {
//    construct(stringLength, tourSize, eval, popSize, flags);
      genomeLen = stringLength;
      setTourParameters(tourSize);
      genomeLen = stringLength;
      evaluator = eval;
      this.flags = flags;
      this.popSize = popSize;
      setOperProb(.25, .01);
      init();
    }
   */
  /*
   private void setTourParameters(int size) {
    tourSize = size;
    tourStartBit = genomeLen;
    tourWordSize = 1;
    int i = 0;
    while ( (1 << i) < tourSize)
      i++;
    tourWordSize = i + 1;
    genomeLen += tourWordSize * tourSize;
   }
   private void construct(int stringLength, int tourSize, IGenEvaluator eval,
                         int popSize,
                         int flags) {
   }
   */

  /**
   * Test method.
   * @param args
   */
  public static void main(String[] args) {
    System.out.println("Population test module");
    Population p = new Population(null, 10, 0);

//    p.addBitField(20);
    p.addTourField(10);
    p.complete();
//    p.clearGenomes(false);
    System.out.println("Initially:");
    p.display();

    p.evolve();
    p.display();
  }

  /**
   * Default evaluator.
   * For test purposes only; evaluates as # of 1 bits.
   */
  public double eval(Genotype g) {
    int v = 0;
    for (int i = 0; i < this.genomeLen; i++) {
      if (g.getBit(i))
        v++;
    }
    return v;
  }

  public String description(Genotype g) {
    return g.toString();
  }

  protected int fieldCount() {
    return fieldCount;
  }

  protected PopField getField(int field) {
    return fields[field];
  }
}
