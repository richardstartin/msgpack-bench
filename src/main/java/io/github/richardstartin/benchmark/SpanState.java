package io.github.richardstartin.benchmark;

import io.github.richardstartin.MySpan;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.openjdk.jmh.annotations.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Benchmark)
public class SpanState {


  @Param("2048")
  int numTraces;

  @Param("5")
  int traceSize;

  @Param
  BufferStrategy bufferStrategy;

  @Param
  MessagePackerStrategy messagePackerStrategy;

  List<MySpan>[] traces;
  MessagePacker packer;
  MessageBufferOutput buffer;

  @Setup(Level.Trial)
  public void init() {
    traces = new List[numTraces];
    for (int i = 0; i < traces.length; ++i) {
      List<MySpan> trace = new ArrayList<>(traceSize);
      for (int j = 0; j < traceSize; ++j) {
        trace.add(newSpan());
      }
      traces[i] = trace;
    }
    buffer = bufferStrategy.newOutput();
  }


  private static MySpan newSpan() {
    // TODO - parameterised data generation, explore tags and metrics
    return new MySpan(BigInteger.valueOf(ThreadLocalRandom.current().nextLong()),
            BigInteger.valueOf(ThreadLocalRandom.current().nextLong()),
            BigInteger.valueOf(ThreadLocalRandom.current().nextLong()),
            ThreadLocalRandom.current().nextLong(),
            ThreadLocalRandom.current().nextLong(),
            "SPAN_TYPE",
            200,
            "cool.service",
            "GET",
            "/admin/console",
            Map.of("foo", "bar", "com", "qux"),
            Map.of("p50", ThreadLocalRandom.current().nextDouble(),
                    "p75", ThreadLocalRandom.current().nextDouble(),
                    "p95", ThreadLocalRandom.current().nextDouble(),
                    "p99", ThreadLocalRandom.current().nextDouble(),
                    "p999", ThreadLocalRandom.current().nextDouble(),
                    "max", ThreadLocalRandom.current().nextDouble()));
  }

}
