package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.sql.Date;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockPrice;
import org.junit.Test;

public class StockPriceTest extends ASpringTest {

  @Test
  public void testBasicConstructorAndGetters() {
//    StockPrice stockPrice = new StockPrice(1, "TEST", new BigDecimal(0), new Date(0));
//    assertEquals(new Long(1), stockPrice.getId());
//    assertEquals("TEST", stockPrice.getStock());
//    assertEquals(new Date(0), stockPrice.getDate());
  }

  @Test
  public void testStockBecomesUpperCase() {
//    StockPrice stockPrice = new StockPrice(1, "test", new BigDecimal(0), new Date(0));
//    assertEquals("TEST", stockPrice.getStock());
  }

  @Test
  public void testStockCannotBeEmpty() {
  }

  @Test
  public void testPriceValidation() {
  }

  @Test
  public void testDateValidation() {
  }

  @Test
  public void testDatabaseStorageAndRetrieval() {
  }
}
