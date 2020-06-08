package org.galatea.starter.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.rpsy.IStockPriceRpsy;
import org.galatea.starter.testutils.TestDataGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class StockPriceServiceTest extends ASpringTest {
  @MockBean
  private IStockPriceRpsy mockStockPriceRpsy;

  @Autowired // may remove annotation and set value in @Before method, see SettlementService.java
  private StockPriceService service;

  /**
   * Test that StockPriceService.findStockPrice returns list of StockPrices found by repository
   */
  @Test
  public void testFindStockPriceFindBySymbolFound() {
    String symbol = "IBM";

    StockPrice testStockPrice = TestDataGenerator.defaultStockPriceData().build();

    given(this.mockStockPriceRpsy.findBySymbolOrderByDateDesc(symbol))
        .willReturn(Collections.singletonList(testStockPrice));

    List<StockPrice> retrieved = service.findStockPricesBySymbol("IBM");
    assertTrue(retrieved.size() == 1);
  }

  /**
   * Test that StockPriceService.findStockPrice returns empty list when not found by repository
   */
  @Test
  public void testFindStockPriceFindBySymbolNotFound() {

  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns correct number of elements
   * when possible.
   */
  @Test
  public void testFindFirstStockPricesSize() {

  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns min(num, list.size) number of elements
   * when given list is too small.
   */
  @Test
  public void testFindFirstStockPricesMinSize() {

  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns empty list when num is 0.
   */
  @Test
  public void testFindFirstStockPricesZero() {

  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns empty list when given list is empty.
   */
  @Test
  public void testFindFirstStockPricesEmptyList() {

  }

  /**
   * Test that StockPriceService.saveStockPricesIfNotExists does not save to database when given
   * list is empty.
   */
  @Test
  public void testSaveStockPricesIfNotExistsEmptyList() {

  }

  /**
   * Test that StockPriceService.saveStockPricesIfNotExists saves to database when not exists.
   */
  @Test
  public void testSaveStockPricesIfNotExistsSavesWhenNotExists() {

  }

  /**
   * Test that StockPriceService.saveStockPricesIfNotExists does not save to database when exists.
   */
  @Test
  public void testSaveStockPricesIfNotExistsDoesNotSaveWhenExists() {

  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns true when days=0.
   */
  @Test
  public void testHasNecessaryStockPricesZeroDays() {

  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns true when days=0 and list.size=0.
   */
  @Test
  public void testHasNecessaryStockPricesZeroDaysAndEmptyList() {

  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns false when days>0 and list.size=0.
   */
  @Test
  public void testHasNecessaryStockPricesEmptyList() {

  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns false when most recent StockPrice
   * has date before most recent weekday.
   */
  @Test
  public void testHasNecessaryStockPricesNotUpToDate() {

  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns false when most recent StockPrice
   * has date = most recent weekday but list.size < days.
   */
  @Test
  public void testHasNecessaryStockPricesNotEnoughHistory() {

  }

  /**
   * Test that StockPriceService.makeApiCall returns non-null JsonNode.
   */
  @Test
  public void testMakeApiCallReturnsNonNullJsonNode() {

  }
}
