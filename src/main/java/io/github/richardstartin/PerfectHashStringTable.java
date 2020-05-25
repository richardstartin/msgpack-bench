package io.github.richardstartin;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PerfectHashStringTable implements PerfectHash, StringTable {


  public static void main(String... args) {
    PerfectHashStringTable table = new PerfectHashStringTable("three", "blind", "mice", "yankee", "doodle");
    System.out.println(new String(table.getEncoded("three")));
    System.out.println(new String(table.getEncoded("blind")));
    System.out.println(new String(table.getEncoded("mice")));
    System.out.println(new String(table.getEncoded("yankee")));
    System.out.println(new String(table.getEncoded("doodle")));
    System.out.println(table.getEncoded("lol"));
  }

  private final EqualityBucket[] equalityBuckets;
  private final int[] values;
  private final int[] seeds;

  @SuppressWarnings("unchecked")
  public PerfectHashStringTable(String... strings) {
    int length = 1 << -Integer.numberOfLeadingZeros(strings.length - 1);
    this.values = new int[length];
    Arrays.fill(values, -1);
    this.seeds = new int[length];
    this.equalityBuckets = new EqualityBucket[strings.length];
    for (int i = 0; i < strings.length; ++i) {
      equalityBuckets[i] = new EqualityBucket(strings[i]);
    }
    List<Bucket>[] buckets = new List[length];
    Arrays.setAll(buckets, i -> new ArrayList<>());
    for (int i = 0; i < strings.length; ++i) {
      String string = strings[i];
      int hash = string.hashCode();
      int modHash = hash & (length - 1);
      buckets[modHash].add(new Bucket(i, hash));
    }
    Arrays.sort(buckets, Comparator.comparingInt(l -> -l.size()));
    BitSet free = new BitSet(values.length);
    free.set(0, values.length);
    int[] entries = new int[length];
    Arrays.fill(entries, -1);
    int b = 0;
    for (; b < buckets.length && buckets[b].size() > 1; b++) {
      List<Bucket> subKeys = buckets[b];
      int seed = 0;
      nextSeed: while (true) {
        seed++;
        boolean marked = false;
        for (Bucket bucket : subKeys) {
          int i = xorShift(bucket.hash + seed) & (length - 1);
          if (entries[i] == -1 && values[i] == -1) {
            marked = true;
            entries[i] = bucket.position;
            continue;
          }
          if (marked) {
            Arrays.fill(entries, -1);
          }
          continue nextSeed;
        }
        break;
      }
      for (int e = 0; e < entries.length; ++e) {
        if (entries[e] != -1) {
          values[e] = entries[e];
          free.clear(e);
        }
      }
      seeds[subKeys.get(0).hash & (length - 1)] = seed;
    }

    int slot = free.nextSetBit(0);
    while (b < buckets.length && !buckets[b].isEmpty()) {
      Bucket bucket = buckets[b].get(0);
      values[slot] = bucket.position;
      seeds[bucket.hash & (length - 1)] = -slot - 1;
      ++b;
      slot = free.nextSetBit(slot);
    }
  }


  @Override
  public int code(String value) {
    int seed = seeds[value.hashCode() & (values.length - 1)];
    return seed < 0
            ? values[-seed-1]
            : values[xorShift(seed + value.hashCode()) & (values.length - 1)];
  }

  @Override
  public byte[] getEncoded(String value) {
    int code = code(value);
    return code >= 0 && value.equals(equalityBuckets[code].string)
            ? equalityBuckets[code].encoding
            : null;
  }

  private static class Bucket {
    int position;
    int hash;

    public Bucket(int position, int hash) {
      this.position = position;
      this.hash = hash;
    }
  }

  private static class EqualityBucket {
    private final String string;
    private final byte[] encoding;

    private EqualityBucket(String string) {
      this.string = string;
      this.encoding = string.getBytes(UTF_8);
    }
  }

  private static int xorShift(int x) {
    x ^= x >>> 12;
    x ^= x << 25; // b
    x ^= x >>> 27; // c
    return (int)(x * 2685821657736338717L);
  }
}
