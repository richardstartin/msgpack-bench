package io.github.richardstartin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FastUtilStringTable implements StringTable {

  private final Object2ObjectOpenHashMap<String, byte[]> table;

  public FastUtilStringTable(String... strings) {
    this.table = new Object2ObjectOpenHashMap<>(strings.length * 2);
    for (String string : strings) {
      table.put(string, string.getBytes(UTF_8));
    }
  }

  @Override
  public byte[] getEncoded(String value) {
    return table.get(value);
  }
}
