package io.github.richardstartin.benchmark;

import org.msgpack.core.buffer.ArrayBufferOutput;
import org.msgpack.core.buffer.MessageBufferOutput;

public enum BufferStrategy {
  ARRAY {
    @Override
    MessageBufferOutput newOutput() {
      return new ArrayBufferOutput();
    }
  }

  ;

  abstract MessageBufferOutput newOutput();
}
