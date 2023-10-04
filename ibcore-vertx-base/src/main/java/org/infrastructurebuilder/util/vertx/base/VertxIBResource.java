package org.infrastructurebuilder.util.vertx.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.infrastructurebuilder.util.readdetect.IBResourceBase;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public interface VertxIBResource extends IBResourceBase {

  Vertx vertx();

  default Optional<Future<AsyncFile>> get() {
    if (getPath().isEmpty())
      return Optional.empty();
    OpenOptions o = new OpenOptions().setRead(true);
    Path p = Paths.get(".");
    return Optional.of(vertx().fileSystem().open(p.toAbsolutePath().toString(), o));
  }

}
