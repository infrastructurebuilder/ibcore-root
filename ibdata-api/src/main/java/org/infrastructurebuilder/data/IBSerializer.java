package org.infrastructurebuilder.data;

import static org.infrastructurebuilder.data.IBDataException.cet;

import java.nio.file.Path;
import java.util.Optional;

public interface IBSerializer<T, C, S extends AutoCloseable> extends AutoCloseable {

  IBSerializer<T, C, S> toPath(Path p);

  IBSerializer<T, C, S> withSerializationConfiguration(C c);

  Optional<S> getSerializer();

  default void close() throws Exception {
    getSerializer().ifPresent(e -> cet.withTranslation(() -> e.close()));
  }

}
