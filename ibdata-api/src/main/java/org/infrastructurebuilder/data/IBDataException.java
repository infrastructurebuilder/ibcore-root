package org.infrastructurebuilder.data;

import org.infrastructurebuilder.IBException;

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;

public class IBDataException extends IBException {
  public static ExceptionTranslator cet = ET.newConfiguration().translate(Exception.class).to(IBDataException.class).done();

  private static final long serialVersionUID = 2000551302082923226L;

  public IBDataException() {
    super();
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public IBDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * @param message
   * @param cause
   */
  public IBDataException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public IBDataException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public IBDataException(Throwable cause) {
    super(cause);
  }

}
