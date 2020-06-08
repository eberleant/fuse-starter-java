package org.galatea.starter.domain;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class PricesTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  /**
   * Valid Prices has no errors.
   */
  @Test
  public void validPrices() {

  }

  /**
   * Open price >= 0.
   */
  @Test
  public void openPriceMustBeNonNegative() {

  }

  /**
   * High price >= 0.
   */
  @Test
  public void highPriceMustBeNonNegative() {

  }

  /**
   * Low price >= 0.
   */
  @Test
  public void lowPriceMustBeNonNegative() {

  }

  /**
   * Close price >= 0.
   */
  @Test
  public void closePriceMustBeNonNegative() {

  }
  
}
