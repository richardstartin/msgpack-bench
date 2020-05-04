package io.github.richardstartin;

import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferOutput;

import java.nio.ByteBuffer;

public class ByteBufferMessageBufferOutput implements MessageBufferOutput {

  private final ByteBuffer buffer;

  public ByteBufferMessageBufferOutput(ByteBuffer buffer) {
    this.buffer = buffer.position(5);
  }

  @Override
  public MessageBuffer next(int i) {
    return MessageBuffer.wrap(buffer);
  }

  @Override
  public void writeBuffer(int i) {
      // only called on flush
  }

  @Override
  public void write(byte[] bytes, int offset, int length) {
    buffer.put(bytes, offset, length);
  }

  @Override
  public void add(byte[] bytes, int offset, int length) {
    write(bytes, offset, length);
  }

  @Override
  public void close() {
    buffer.position(5).limit(buffer.capacity());
  }

  @Override
  public void flush() {

  }
}
