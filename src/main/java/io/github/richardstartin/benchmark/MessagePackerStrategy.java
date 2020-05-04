package io.github.richardstartin.benchmark;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.MessageBufferOutput;

public enum MessagePackerStrategy {
  DISPOSE {
    @Override
    MessagePacker getMessagePacker(MessageBufferOutput buffer) {
      return MessagePack.newDefaultPacker(buffer);
    }
  };

  abstract MessagePacker getMessagePacker(MessageBufferOutput buffer);

}
