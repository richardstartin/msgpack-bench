package io.github.richardstartin;

import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HashMapStringTable implements StringTable{

  private final HashMap<String, byte[]> table;

  public HashMapStringTable(String[] strings) {
    table = new HashMap<>((strings.length * 4) / 3);
    for (String string : strings) {
      table.put(string, string.getBytes(UTF_8));
    }
  }


  @Override
  public byte[] getEncoded(String value) {
    return table.get(value);
  }
}
