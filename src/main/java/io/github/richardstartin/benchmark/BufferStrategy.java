package io.github.richardstartin.benchmark;

import io.github.richardstartin.ByteBufferMessageBufferOutput;
import org.msgpack.core.buffer.ArrayBufferOutput;
import org.msgpack.core.buffer.MessageBufferOutput;

import java.io.IOException;
import java.nio.ByteBuffer;

public enum BufferStrategy {
  ARRAY {
    @Override
    void close(MessageBufferOutput output) throws IOException {
      output.close();
    }

    @Override
    MessageBufferOutput newOutput() {
      return new ArrayBufferOutput();
    }

    @Override
    ByteBuffer shareBuffer() {
      return null;
    }
  },
  BUFFER {
    final ByteBuffer buffer = ByteBuffer.allocate(10 << 40);
    final ByteBufferMessageBufferOutput output = new ByteBufferMessageBufferOutput(buffer);

    @Override
    void close(MessageBufferOutput output) throws IOException {
      output.close();
    }

    @Override
    MessageBufferOutput newOutput() {
      return output;
    }

    @Override
    ByteBuffer shareBuffer() {
      return buffer;
    }
  },


  ;
  abstract void close(MessageBufferOutput output) throws IOException;
  abstract MessageBufferOutput newOutput();
  abstract ByteBuffer shareBuffer();
}
