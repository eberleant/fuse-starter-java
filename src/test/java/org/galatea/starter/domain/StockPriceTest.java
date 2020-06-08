package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.sql.Date;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockPrice;
import org.junit.BeforeClass;
import org.junit.Test;

public class StockPriceTest extends ASpringTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  /**
   * Valid StockPrice has no errors.
   */
  @Test
  public void validStockPrice() {

  }

  /**
   * Symbol becomes upper case when set.
   */
  @Test
  public void symbolBecomesUpperCase() {

  }

  /**
   * Symbol cannot be empty.
   */
  @Test
  public void symbolCannotBeEmpty() {
  }

  /**
   * Symbol cannot be null.
   */
  @Test
  public void symbolNonNull() {

  }

  /**
   * Date cannot be null.
   */
  @Test
  public void dateNonNull() {

  }

  /**
   * Date cannot be in the future.
   */
  @Test
  public void dateMustBeBeforeNow() {

  }

  /**
   * Prices is valid (errors in Prices object show here).
   */
  @Test
  public void pricesMustBeValid() {

  }

  /**
   * Equals returns true when symbol, date, and prices are equal.
   */
  @Test
  public void testEqualsTrue() {

  }

  /**
   * Equals returns true when any of symbol, date, or prices are not equal.
   */
  @Test
  public void testEqualsFalse() {

  }

}
