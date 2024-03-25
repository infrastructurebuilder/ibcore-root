package org.infrastructurebuilder.util.vertx.base;

import static io.vertx.core.Future.failedFuture;
import static java.util.Optional.ofNullable;

import javax.annotation.Nullable;

import org.infrastructurebuilder.util.core.OptStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;

public class FutureStream {

  public final static Logger log = LoggerFactory.getLogger(OptStream.class);
  private final Future<AsyncFile> stream;

  public FutureStream(@Nullable Future<AsyncFile> ins) {
    this.stream = ins;
  }

  public final Future<AsyncFile> getStream() {
    return ofNullable(this.stream).orElse(failedFuture("no.stream.available"));
  }

}
