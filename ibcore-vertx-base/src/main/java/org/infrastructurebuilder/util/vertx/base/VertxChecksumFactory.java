/*
 * @formatter:off
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */
package org.infrastructurebuilder.util.vertx.base;

import static org.infrastructurebuilder.constants.IBConstants.DIGEST_TYPE;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.pathref.Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.file.OpenOptions;

public class VertxChecksumFactory {

  private final static Logger log = LoggerFactory.getLogger(VertxChecksumFactory.class);

  private static final int EIGHTK = 8192;

  private final Vertx vertx;

  public final static Future<Checksum> checksumFrom(Vertx v, Path p) {
    return Optional.ofNullable(v).map(vertx -> {
      Promise<Checksum> f = Promise.promise();
      vertx.fileSystem().open(p.toString(), new OpenOptions().setWrite(false).setCreate(false), res -> {
        if (res.succeeded()) {
          final MessageDigest md;
          try {
            md = MessageDigest.getInstance(DIGEST_TYPE);
          } catch (NoSuchAlgorithmException e) {
            f.fail(e);
            return;
          }
          res.result().setReadBufferSize(EIGHTK).handler(h -> {
            md.update(h.getBytes());
          }).endHandler(eh -> {
            f.complete(new Checksum(md.digest()));
          });
        } else {
          f.fail(res.cause());
        }
      });
      return f.future();
    }).orElse(Future.failedFuture("no.vertx"));
  }

  public VertxChecksumFactory() {
    this(Vertx.vertx());
  }

  public VertxChecksumFactory(Vertx v) {
    this.vertx = Objects.requireNonNull(v);
  }

  public final Future<Checksum> checksumFrom(Path p) {
    return VertxChecksumFactory.checksumFrom(vertx, p);
  }

}
