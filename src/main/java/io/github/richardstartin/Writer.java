package io.github.richardstartin;

import org.msgpack.core.MessagePacker;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.msgpack.core.MessagePack.UTF8;

public class Writer {

  public static final Writer WRITER = new Writer();
  private static final HashMap<String, byte[]> INTERN = new HashMap<>();

  @FunctionalInterface
  interface NumberWriter {
    void write(Number value, MessagePacker packer) throws IOException;
  }

  private static final ClassValue<NumberWriter> NUMBER_WRITERS = new ClassValue<>() {
    private final IllegalArgumentException EX = new IllegalArgumentException("Bad type");

    @Override
    protected NumberWriter computeValue(Class<?> type) {
      if (type == Double.class) {
        return (v, mp) -> mp.packDouble(v.doubleValue());
      }
      if (type == Long.class) {
        return (v, mp) -> mp.packLong(v.longValue());
      }
      if (type == Integer.class) {
        return (v, mp) -> mp.packInt(v.intValue());
      }
      if (type == Float.class) {
        return (v, mp) -> mp.packFloat(v.floatValue());
      }
      if (type == Byte.class) {
        return (v, mp) -> mp.packShort(v.byteValue());
      }
      if (type == Short.class) {
        return (v, mp) -> mp.packShort(v.shortValue());
      }
      throw EX;
    }
  };

  public void write(String value, MessagePacker packer) throws IOException {
    byte[] bytes = INTERN.get(value);
    if (null == bytes) {
      synchronized (INTERN) {
        bytes = value.getBytes(UTF8);
        INTERN.put(value, bytes);
      }
    }
    packer.packRawStringHeader(bytes.length);
    packer.addPayload(bytes);
  }


  public void write(String key, String value, MessagePacker packer) throws IOException {
    write(key, packer);
    write(value, packer);
  }

  public void write(String key, double value, MessagePacker packer) throws IOException {
    write(key, packer);
    packer.packDouble(value);
  }


  public void write(String key, long value, MessagePacker packer) throws IOException {
    write(key, packer);
    packer.packLong(value);
  }

  public void write(String key, int value, MessagePacker packer) throws IOException {
    write(key, packer);
    packer.packInt(value);
  }

  public void writeNumberViaMap(String key, Number value, MessagePacker packer) throws IOException {
    write(key, packer);
    if (null != value) {
      NUMBER_WRITERS.get(value.getClass())
              .write(value, packer);
    }
  }

  public void writeNumber(String key, Number value, MessagePacker packer) throws IOException {
    write(key, packer);
    if (value instanceof Double) {
      write(key, value.doubleValue(), packer);
    } else if (value instanceof Long) {
      write(key, value.longValue(), packer);
    } else if (value instanceof Integer) {
      write(key, value.intValue(), packer);
    } else if (value instanceof Float) {
      write(key, value.floatValue(), packer);
    } else if (value instanceof Byte) {
      write(key, value.byteValue(), packer);
    } else if (value instanceof Short) {
      write(key, value.shortValue(), packer);
    }
  }

  public void writeNull(MessagePacker packer) throws IOException {
    packer.packNil();
  }

  public void write(String key, BigInteger value, MessagePacker packer) throws IOException {
    write(key, packer);
    if (null == value) {
      writeNull(packer);
    } else {
      packer.packBigInteger(value);
    }
  }

  public void writeNumbers(String key, Map<String, Number> value, MessagePacker packer) throws IOException {
    write(key, packer);
    packer.packMapHeader(value.size());
    for (Map.Entry<String, Number> entry : value.entrySet()) {
      writeNumber(entry.getKey(), entry.getValue(), packer);
    }
  }

  public void write(String key, Map<String, String> value, MessagePacker packer) throws IOException {
    write(key, packer);
    packer.packMapHeader(value.size());
    for (Map.Entry<String, String> entry : value.entrySet()) {
      write(entry.getKey(), entry.getValue(), packer);
    }
  }

  public void writeSpan(MySpan span, MessagePacker packer) throws IOException {
    // Some of the tests rely on the specific ordering here.
    packer.packMapHeader(12); // must match count below.
    write("service", span.serviceName, packer);
    write("name", span.operationName, packer);
    write("resource", span.resourceName, packer);
    write("trace_id", span.traceId, packer);
    write("span_id", span.spanId, packer);
    write("parent_id", span.parentId, packer);
    write("start", span.startTime, packer);
    write("duration", span.duration, packer);
    write("type", span.type, packer);
    write("error", span.error, packer);
    writeNumbers("metrics", span.metrics, packer);
    write("meta", span.tags, packer);
  }
}



