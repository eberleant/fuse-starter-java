package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.testutils.TestDataGenerator;
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
    StockPrice sp = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(StockPriceInfo.builder()
            .open(new BigDecimal(0))
            .high(new BigDecimal(0))
            .low(new BigDecimal(0))
            .close(new BigDecimal(0)).build()).build();

    Set<ConstraintViolation<StockPrice>> constraintViolations = validator.validate(sp);

    assertEquals(0, constraintViolations.size());
  }

  /**
   * Symbol must be made of only uppercase letters and numbers.
   */
  @Test
  public void symbolMustBeUppercaseLettersAndNumbers() {
    String symbol = "ibm";
    StockPrice sp = StockPrice.builder()
        .symbol(symbol)
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build()).build();

    Set<ConstraintViolation<StockPrice>> constraintViolations = validator.validate(sp);

    assertEquals("Symbol must consist of only uppercase letters and numbers",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  /**
   * Symbol must not be an empty String.
   */
  @Test
  public void symbolMustNotBeEmpty() {
    StockPrice sp = StockPrice.builder()
        .symbol("")
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build()).build();

    Set<ConstraintViolation<StockPrice>> constraintViolations = validator.validate(sp);

    assertEquals("Symbol must not be empty",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  /**
   * Symbol cannot be null.
   */
  @Test
  public void symbolNonNull() {
    try {
      StockPrice.builder()
          .date(new Date(0))
          .prices(TestDataGenerator.defaultStockPriceInfoData().build()).build();
      assertTrue("StockPrice with null symbol was allowed to be built", false);
    } catch (NullPointerException npe) {
      assertTrue("Null pointer exception thrown when symbol is null", true);
    }
  }

  /**
   * Date cannot be null.
   */
  @Test
  public void dateNonNull() {
    try {
      StockPrice.builder()
          .symbol("IBM")
          .prices(TestDataGenerator.defaultStockPriceInfoData().build()).build();
      assertTrue("StockPrice with null date was allowed to be built", false);
    } catch (NullPointerException npe) {
      assertTrue("Null pointer exception thrown when date is null", true);
    }
  }

  /**
   * Prices is valid (errors in Prices object show here).
   */
  @Test
  public void pricesMustBeValid() {
    StockPrice sp = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(StockPriceInfo.builder()
            .open(new BigDecimal(-1))
            .high(new BigDecimal(0))
            .low(new BigDecimal(0))
            .close(new BigDecimal(0)).build()).build();

    Set<ConstraintViolation<StockPrice>> constraintViolations = validator.validate(sp);

    assertEquals("Open price must be greater than or equal to 0.00",
        constraintViolations.iterator().next().getMessage());
    assertEquals(1, constraintViolations.size());
  }

  /**
   * Equals returns true when symbol, date, and prices are equal.
   */
  @Test
  public void testEqualsTrue() {
    StockPrice sp1 = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build())
        .id(1L).build();
    StockPrice sp2 = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build())
        .id(2L).build();

    assertTrue(sp1.equals(sp2));
  }

  /**
   * Equals returns true when any of symbol, date, or prices are not equal.
   */
  @Test
  public void testEqualsFalse() {
    StockPrice sp1 = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build())
        .id(1L).build();
    StockPrice sp2 = StockPrice.builder()
        .symbol("DNKN")
        .date(sp1.getDate())
        .prices(sp1.getPrices())
        .id(sp1.getId()).build();

    assertFalse(sp1.equals(sp2)); // different symbols

    sp2.setSymbol(sp1.getSymbol());
    sp2.setDate(new Date(1));

    assertFalse(sp1.equals(sp2)); // different dates

    sp2.setDate(sp1.getDate());
    sp2.setPrices(StockPriceInfo.builder()
        .open(new BigDecimal(1))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0)).build());

    assertFalse(sp1.equals(sp2)); // different prices
  }

}
