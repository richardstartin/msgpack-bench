package io.github.richardstartin.benchmark;

import io.github.richardstartin.MySpan;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.ArrayBufferOutput;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.List;

import static io.github.richardstartin.Writer.WRITER;

public class TraceSerializationBenchmark {

  @Benchmark
  public void serializeTraceBaseline(SpanState spanState, Blackhole bh) throws IOException {
    for (List<MySpan> trace : spanState.traces) {
      MessageBufferOutput buffer = spanState.bufferStrategy.newOutput();
      MessagePacker packer = spanState.messagePackerStrategy.getMessagePacker(buffer);
      for (MySpan span : trace) {
        WRITER.writeSpan(span, packer);
      }
      if (buffer instanceof ArrayBufferOutput) {
        bh.consume(((ArrayBufferOutput)buffer).toByteArray());
      } else {
        bh.consume(buffer);
      }
      spanState.bufferStrategy.close(buffer);
      packer.close();
    }
  }

  @Benchmark
  public void serializeTraceReuseBuffer(SpanState spanState, Blackhole bh) throws IOException {
    MessageBufferOutput buffer = spanState.bufferStrategy.newOutput();
    MessagePacker packer = spanState.messagePackerStrategy.getMessagePacker(buffer);
    for (List<MySpan> trace : spanState.traces) {
      for (MySpan span : trace) {
        WRITER.writeSpan(span, packer);
      }
    }
    bh.consume(spanState.buffer);
    spanState.bufferStrategy.close(buffer);
    packer.close();
  }
}
