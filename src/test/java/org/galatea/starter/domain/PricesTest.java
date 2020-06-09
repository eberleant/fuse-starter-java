package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Set;
import javax.validation.ConstraintViolation;
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
    Prices p = Prices.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0)).build();

    Set<ConstraintViolation<Prices>> constraintViolations = validator.validate(p);

    assertEquals(0, constraintViolations.size());
  }

  /**
   * Open price >= 0.
   */
  @Test
  public void openPriceMustBeNonNegative() {
    Prices p = Prices.builder()
        .open(new BigDecimal(-1))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0)).build();

    Set<ConstraintViolation<Prices>> constraintViolations = validator.validate(p);

    assertEquals("Open price must be greater than or equal to 0.00",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  /**
   * High price >= 0.
   */
  @Test
  public void highPriceMustBeNonNegative() {
    Prices p = Prices.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(-1))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0)).build();

    Set<ConstraintViolation<Prices>> constraintViolations = validator.validate(p);

    assertEquals("High price must be greater than or equal to 0.00",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  /**
   * Low price >= 0.
   */
  @Test
  public void lowPriceMustBeNonNegative() {
    Prices p = Prices.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(0))
        .low(new BigDecimal(-1))
        .close(new BigDecimal(0)).build();

    Set<ConstraintViolation<Prices>> constraintViolations = validator.validate(p);

    assertEquals("Low price must be greater than or equal to 0.00",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  /**
   * Close price >= 0.
   */
  @Test
  public void closePriceMustBeNonNegative() {
    Prices p = Prices.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(-1)).build();

    Set<ConstraintViolation<Prices>> constraintViolations = validator.validate(p);

    assertEquals("Close price must be greater than or equal to 0.00",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }
  
}
