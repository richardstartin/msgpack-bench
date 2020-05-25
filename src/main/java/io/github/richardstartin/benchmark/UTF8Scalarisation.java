package io.github.richardstartin.benchmark;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Thread)
public class UTF8Scalarisation {

  private static final byte[] LOWER_CASE = new byte[26];
  static {
    for (int i = 'a'; i <= 'z'; ++i) {
      LOWER_CASE[i - 'a'] = (byte)i;
    }
  }

  public static String create(int size) {
    byte[] bytes = new byte[size];
    for (int i = 0; i < size; ++i) {
      bytes[i] = LOWER_CASE[ThreadLocalRandom.current().nextInt(26)];
    }
    return new String(bytes, StandardCharsets.UTF_8);
  }


  @Param({"0", "4", "32", "63", "64", "128", "256", "512"})
  int size;

  String data;

  @Setup(Level.Trial)
  public void init() {
    data = create(size);
  }


  @Benchmark
  public byte[] getUTF8() {
    return data.getBytes(StandardCharsets.UTF_8);
  }


  @Benchmark
  public byte[] getLatin1() {
    return data.getBytes(StandardCharsets.ISO_8859_1);
  }

  @Benchmark
  public int getUTF8Length() {
    return data.getBytes(StandardCharsets.UTF_8).length;
  }

  @Benchmark
  public int getLatin1Length() {
    return data.getBytes(StandardCharsets.ISO_8859_1).length;
  }

  @Benchmark
  public byte[] getUTF8Conditional() {
    byte[] utf8;
    if (ThreadLocalRandom.current().nextBoolean()) {
      utf8 = data.getBytes(StandardCharsets.UTF_8);
    } else {
      utf8 = new byte[0];
    }
    return utf8;
  }

  @Benchmark
  public int getUTF8LengthConditional() {
    int length;
    if (ThreadLocalRandom.current().nextBoolean()) {
      length = data.getBytes(StandardCharsets.UTF_8).length;
    } else {
      length = 0;
    }
    return length;
  }


}
