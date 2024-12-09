package org.infrastructurebuilder.api.base;

import java.util.Optional;

import javax.annotation.Nullable;

public interface WithString<R>  {
  /**
   * Returns an optional item if the object supplied (or null) can produce an instance of that object
   *
   * @param t A String (or null) that is passed into the call to configure or otherwise be used to provide the
   *          resulting object
   * @return an optional instance of R
   */
  Optional<R> with(@Nullable String t);

  /**
   * This is the class that the object supplied in the with call above is to be supplied. By convention, it should be
   * String.class
   *
   * @return
   */
  default Class<String> withClass() {
    return String.class;
  }

}
