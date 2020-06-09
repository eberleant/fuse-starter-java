package org.galatea.starter.entrypoint.exception;

public class DataNotFoundException extends RuntimeException {

  /**
   * Create a DataNotFoundException.
   */
  public DataNotFoundException(final String symbol) {
    super("No data could be found for symbol '" + symbol + "'");
  }
}
