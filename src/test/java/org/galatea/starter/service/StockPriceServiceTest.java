package org.galatea.starter.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.rpsy.IStockPriceRpsy;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.Helpers;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

// from https://stackoverflow.com/questions/21271468/spring-propertysource-using-yaml
// why does it work?
@TestPropertySource("classpath:application.yml")
@ContextConfiguration(initializers = {ConfigFileApplicationContextInitializer.class})
@Import({StockPriceService.class})
public class StockPriceServiceTest extends ASpringTest {
  @MockBean
  private IStockPriceRpsy mockStockPriceRpsy;

  @Autowired // may remove annotation and set value in @Before method, see SettlementService.java
  private StockPriceService service;

  @Value("${alpha-vantage.api-key}")
  private String apiKey;

  @Value("${alpha-vantage.basePath}")
  private String basePath;

  /**
   * Test that StockPriceService.findStockPricesBySymbol returns list of StockPrices found by
   * repository.
   */
  @Test
  public void testFindStockPricesSymbolFound() {
    String symbol = "IBM";

    StockPrice testStockPrice = TestDataGenerator.defaultStockPriceData().build();

    BDDMockito.given(this.mockStockPriceRpsy.findBySymbolIgnoreCaseOrderByDateDesc(symbol))
        .willReturn(Collections.singletonList(testStockPrice));

    List<StockPrice> retrieved = service.findStockPricesBySymbol("IBM");
    assertTrue(retrieved.size() == 1 && retrieved.get(0).equals(testStockPrice));
  }

  /**
   * Test that StockPriceService.findStockPricesBySymbol returns empty list when all StockPrices in
   * database have a different symbol.
   */
  @Test
  public void testFindStockPricesBySymbolNotFound() {
    String symbol = "IBM";

    StockPrice testStockPrice = TestDataGenerator.defaultStockPriceData().build();

    BDDMockito.given(this.mockStockPriceRpsy.findBySymbolIgnoreCaseOrderByDateDesc(symbol))
        .willReturn(Collections.singletonList(testStockPrice));

    List<StockPrice> retrieved = service.findStockPricesBySymbol("DNKN"); // different symbol
    assertTrue(retrieved.isEmpty());
  }

  /**
   * Test that StockPriceService.findStockPricesBySymbol returns the list in the same order as the
   * list given by the repository.
   */
  @Test
  public void testFindStockPricesBySymbolMaintainsOrder() {
    String symbol = "IBM";
    // generate 5 StockPrices
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 5);
    Collections.shuffle(stockPrices);

    BDDMockito.given(this.mockStockPriceRpsy.findBySymbolIgnoreCaseOrderByDateDesc(symbol))
        .willReturn(stockPrices);

    List<StockPrice> retrieved = service.findStockPricesBySymbol(symbol);
    assertTrue(retrieved.equals(stockPrices));
  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns correct number of elements
   * when possible.
   */
  @Test
  public void testFindFirstStockPricesSize() {
    // generate 10 StockPrices
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);

    List<StockPrice> retrieved = service.findFirstStockPrices(stockPrices, 5);
    assertTrue(retrieved.size() == 5);
  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns entire list when list.size < num.
   */
  @Test
  public void testFindFirstStockPricesWithSmallList() {
    // generate 10 StockPrices
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);

    List<StockPrice> retrieved = service.findFirstStockPrices(stockPrices, 5);
    assertTrue(retrieved.size() == 5);
  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns empty list when num is 0.
   */
  @Test
  public void testFindFirstStockPricesZero() {
    // generate 10 StockPrices
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);

    List<StockPrice> retrieved = service.findFirstStockPrices(stockPrices, 0);
    assertTrue(retrieved.isEmpty());
  }

  /**
   * Test that StockPriceService.findFirstStockPrices returns empty list when given list is empty.
   */
  @Test
  public void testFindFirstStockPricesEmptyList() {
    List<StockPrice> stockPrices = new ArrayList<>();

    List<StockPrice> retrieved = service.findFirstStockPrices(stockPrices, 10);
    assertTrue(retrieved.isEmpty());
  }

  /**
   * Test that StockPriceService.saveStockPricesIfNotExists does not save to database when given
   * list is empty.
   */
  @Test
  public void testSaveStockPricesIfNotExistsEmptyList() {
    List<StockPrice> stockPrices = new ArrayList<>();

    BDDMockito.given(this.mockStockPriceRpsy.findBySymbolIgnoreCaseAndDate(anyString(), any()))
        .willReturn(new ArrayList<>());

    List<StockPrice> saved = service.saveStockPricesIfNotExists(stockPrices);
    assertTrue(saved.isEmpty());
  }

  /**
   * Test that StockPriceService.saveStockPricesIfNotExists saves to database when not exists.
   */
  @Test
  public void testSaveStockPricesIfNotExistsSavesWhenNotExists() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);
    stockPrices.sort(Comparator.comparing(StockPrice::getId));

    BDDMockito.given(this.mockStockPriceRpsy.findBySymbolIgnoreCaseAndDate(anyString(), any()))
        .willReturn(new ArrayList<>());

    List<StockPrice> saved = service.saveStockPricesIfNotExists(stockPrices);
    saved.sort(Comparator.comparing(StockPrice::getId));
    assertTrue(saved.equals(stockPrices));
  }

  /**
   * Test that StockPriceService.saveStockPricesIfNotExists does not save to database when exists.
   */
  @Test
  public void testSaveStockPricesIfNotExistsDoesNotSaveWhenExists() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);

    BDDMockito.given(this.mockStockPriceRpsy.findBySymbolIgnoreCaseAndDate(any(), any()))
        .willReturn(Collections.singletonList(stockPrices.get(0)));

    List<StockPrice> saved = service.saveStockPricesIfNotExists(stockPrices);
    assertTrue(saved.isEmpty());
  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns true when days=0.
   */
  @Test
  public void testHasNecessaryStockPricesZeroDays() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);
    assertTrue(service.hasNecessaryStockPrices(stockPrices, 0));
  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns true when days=0 and list.size=0.
   */
  @Test
  public void testHasNecessaryStockPricesZeroDaysAndEmptyList() {
    List<StockPrice> stockPrices = new ArrayList<>();
    assertTrue(service.hasNecessaryStockPrices(stockPrices, 0));
  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns false when days>0 and list.size=0.
   */
  @Test
  public void testHasNecessaryStockPricesEmptyList() {
    List<StockPrice> stockPrices = new ArrayList<>();
    assertFalse(service.hasNecessaryStockPrices(stockPrices, 5));
  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns false when first StockPrice
   * has date before most recent weekday.
   */
  @Test
  public void testHasNecessaryStockPricesNotUpToDate() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);
    stockPrices.set(0, StockPrice.builder()
        .date(new Date(0))
        .prices(TestDataGenerator.defaultPricesData().build())
        .symbol("IBM").build());
    assertFalse(service.hasNecessaryStockPrices(stockPrices, 5));
  }

  /**
   * Test that StockPriceService.hasNecessaryStockPrices returns false when most recent StockPrice
   * has date = most recent weekday but list.size < days.
   */
  @Test
  public void testHasNecessaryStockPricesNotEnoughHistory() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);
    stockPrices.set(0, StockPrice.builder()
        .date(Helpers.getMostRecentWeekday())
        .prices(TestDataGenerator.defaultPricesData().build())
        .symbol("IBM").build());
    assertFalse(service.hasNecessaryStockPrices(stockPrices, 15));
  }

  /**
   * StockPriceService.hasNecessaryStockPrices returns true when most recent StockPrice has
   * date = most recent weekday and list.size == days.
   */
  @Test
  public void testHasNecessaryStockPricesEqualSize() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);
    stockPrices.set(0, StockPrice.builder()
        .date(Helpers.getMostRecentWeekday())
        .prices(TestDataGenerator.defaultPricesData().build())
        .symbol("IBM").build());
    assertTrue(service.hasNecessaryStockPrices(stockPrices, 10));
  }

  /**
   * StockPriceService.hasNecessaryStockPrices returns true when most recent StockPrice has
   * date = most recent weekday and list.size > days.
   */
  @Test
  public void testHasNecessaryStockPricesGreaterSize() {
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices("IBM", 10);
    stockPrices.set(0, StockPrice.builder()
        .date(Helpers.getMostRecentWeekday())
        .prices(TestDataGenerator.defaultPricesData().build())
        .symbol("IBM").build());
    assertTrue(service.hasNecessaryStockPrices(stockPrices, 5));
  }

  /**
   * Test that StockPriceService.makeApiCall returns non-empty JsonNode.
   */
  @Test
  public void testMakeApiCallReturnsNonEmptyJsonNode() {
    JsonNode jsonNode = service.makeApiCall(
        new ObjectMapper(), apiKey, basePath, "IBM", "compact");
    assertTrue(jsonNode.fields().hasNext());
  }

  @Configuration
  static class TestConfig {

    @Bean
    PropertySourcesPlaceholderConfigurer propertiesResolver() {
      return new PropertySourcesPlaceholderConfigurer();
    }
  }
}
