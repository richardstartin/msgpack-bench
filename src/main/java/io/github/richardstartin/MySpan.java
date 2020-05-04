package io.github.richardstartin;

import java.math.BigInteger;
import java.util.Map;

public class MySpan {

  BigInteger traceId;
  BigInteger spanId;
  BigInteger parentId;
  long startTime;
  long duration;
  String type;
  int error;
  String serviceName;
  String operationName;
  String resourceName;
  Map<String, String> tags;
  Map<String, Number> metrics;

  public MySpan(BigInteger traceId,
                BigInteger spanId,
                BigInteger parentId,
                long startTime,
                long duration,
                String type,
                int error,
                String serviceName,
                String operationName,
                String resourceName,
                Map<String, String> tags,
                Map<String, Number> metrics) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.parentId = parentId;
    this.startTime = startTime;
    this.duration = duration;
    this.type = type;
    this.error = error;
    this.serviceName = serviceName;
    this.operationName = operationName;
    this.resourceName = resourceName;
    this.tags = tags;
    this.metrics = metrics;
  }
}
