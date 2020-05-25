package io.github.richardstartin.benchmark;

import io.github.richardstartin.FastUtilStringTable;
import io.github.richardstartin.HashMapStringTable;
import io.github.richardstartin.PerfectHashStringTable;
import io.github.richardstartin.StringTable;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;

public class UTF8Deduplication {

  @State(Scope.Benchmark)
  public static abstract class StringTableState {
    private static final byte[] LOWER_CASE = new byte[26];
    private static final byte[] UPPER_CASE = new byte[26];
    static {
      for (int i = 'a'; i <= 'z'; ++i) {
        LOWER_CASE[i - 'a'] = (byte)i;
      }
      for (int i = 'A'; i <= 'Z'; ++i) {
        UPPER_CASE[i - 'A'] = (byte)i;
      }
    }

    String create(int size, boolean upper) {
      byte[] symbols = upper ? UPPER_CASE : LOWER_CASE;
      byte[] bytes = new byte[size];
      for (int i = 0; i < size; ++i) {
        bytes[i] = symbols[random.nextInt(26)];
      }
      return new String(bytes, StandardCharsets.UTF_8);
    }


    @Param({"4", "32", "64", "128"})
    int size;

    @Param({"100", "150", "200"})
    int count;

    @Param("1010101")
    long seed;

    StringTable table;
    String[] stringsInTheTable;
    String[] stringsNotInTheTable;
    SplittableRandom random;

    protected String[] createStrings(boolean upper) {
      String[] strings = new String[count];
      for (int i = 0; i < count; ++i) {
        strings[i] = create(size, upper);
      }
      return strings;
    }

    @Setup(Level.Trial)
    public void init() {
      random = new SplittableRandom(seed);
      this.stringsInTheTable = createStrings(false);
      this.stringsNotInTheTable = createStrings(true);
      this.table = create(stringsInTheTable);
    }


    abstract StringTable create(String[] strings);
  }


  public static class HashMapStringTableState extends StringTableState {

    @Override
    StringTable create(String[] strings) {
      return new HashMapStringTable(strings);
    }
  }

  public static class FastUtilStringTableState extends StringTableState {

    @Override
    StringTable create(String[] strings) {
      return new FastUtilStringTable(strings);
    }
  }

  public static class PerfectHashStringTableState extends StringTableState {

    @Override
    StringTable create(String[] strings) {
      return new PerfectHashStringTable(strings);
    }
  }

  @Benchmark
  public void hashMapPresent(HashMapStringTableState state, Blackhole bh) {
    benchmark(state.table, state.stringsInTheTable, bh);
  }

  @Benchmark
  public void hashMapMissing(HashMapStringTableState state, Blackhole bh) {
    benchmark(state.table, state.stringsNotInTheTable, bh);
  }

  @Benchmark
  public void fastUtilPresent(FastUtilStringTableState state, Blackhole bh) {
    benchmark(state.table, state.stringsInTheTable, bh);
  }

  @Benchmark
  public void fastUtilMissing(FastUtilStringTableState state, Blackhole bh) {
    benchmark(state.table, state.stringsNotInTheTable, bh);
  }


  @Benchmark
  public void perfectHashPresent(PerfectHashStringTableState state, Blackhole bh) {
    benchmark(state.table, state.stringsInTheTable, bh);
  }

  @Benchmark
  public void perfectHashMissing(PerfectHashStringTableState state, Blackhole bh) {
    benchmark(state.table, state.stringsNotInTheTable, bh);
  }



  private void benchmark(StringTable table, String[] strings, Blackhole bh) {
    for (String string : strings) {
      bh.consume(table.getEncoded(string));
    }
  }
}
