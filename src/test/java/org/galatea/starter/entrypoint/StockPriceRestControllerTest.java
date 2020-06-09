package org.galatea.starter.entrypoint;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSender;
import io.restassured.response.ResponseOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.service.StockPriceService;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;

@Slf4j
// is this necessary (had to add to SettlementRestControllerTest)
@SpringBootTest
// for running parameterized tests (run same test multiple times with different sets of parameters)
@RunWith(JUnitParamsRunner.class)
public class StockPriceRestControllerTest extends ASpringTest {

  @Value("${mvc.getPricePath}")
  String pricePath;

  @Autowired
  private ITranslator<JsonNode, List<StockPrice>> timeSeriesJsonTranslator;

  @MockBean
  private StockPriceService mockStockPriceService;

  @Autowired
  private StockPriceRestController stockPriceRestController;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void setup() {
    Map<String, MediaType> mediaTypes = new HashMap<>();
    mediaTypes.put("json", MediaType.APPLICATION_JSON);

    ParameterContentNegotiationStrategy parameterContentNegotiationStrategy =
        new ParameterContentNegotiationStrategy(mediaTypes);

    ContentNegotiationManager manager =
        new ContentNegotiationManager(parameterContentNegotiationStrategy);

    // REST assured with Hamcrest: https://www.baeldung.com/rest-assured-tutorial
    RestAssuredMockMvc.standaloneSetup(
        MockMvcBuilders.standaloneSetup(stockPriceRestController).
            addPlaceholderValue("mvc.getPricePath", pricePath).
            setContentNegotiationManager(manager).
            setMessageConverters(new MappingJackson2HttpMessageConverter()).
            setControllerAdvice(new RestExceptionHandler()));
  }

  private ResponseOptions callGetPrices(String symbol, int days) {
    return callGetPrices(pricePath + "?days=" + days + "&symbol=" + symbol);
  }

  private ResponseOptions callGetPrices(String url) {
    return given()
        .log().ifValidationFails()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(url);
  }

  /**
   * Test that returned JSON from /price has correct metadata fields and info when no API call
   * is needed.
   */
  @SneakyThrows
  @Test
  public void testGetPricesNoAPICallMetadata_JSON() {
    String symbol = "IBM";
    int days = 5;
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 10);

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(stockPrices);
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(stockPrices, days))
        .willReturn(true);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(stockPrices, days))
        .willCallRealMethod();

    ResponseOptions response = callGetPrices(symbol, days);

    JsonNode metadata = objectMapper.readTree(response.getBody().asString()).get("metadata");
    assertEquals(symbol, metadata.get("symbol").textValue());
    assertEquals(days, metadata.get("days").intValue());
  }

  /**
   * Test that returned JSON from /price has correct data array size when no API call is needed.
   */
  @SneakyThrows
  @Test
  public void testGetPricesNoAPICallDataSize_JSON() {
    String symbol = "IBM";
    int days = 5;
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 10);

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(stockPrices);
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(stockPrices, days))
        .willReturn(true);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(stockPrices, days))
        .willCallRealMethod();

    ResponseOptions response = callGetPrices(symbol, days);

    JsonNode data = objectMapper.readTree(response.getBody().asString()).get("data");
    assertEquals(days, data.size());
  }

  /**
   * Test that returned JSON from /price has correct fields for each element of data array when no
   * API call is needed.
   */
  @SneakyThrows
  @Test
  public void testGetPricesNoAPICallDataFields_JSON() {
    String symbol = "IBM";
    int days = 5;
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 10);

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(stockPrices);
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(stockPrices, days))
        .willReturn(true);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(stockPrices, days))
        .willCallRealMethod();

    ResponseOptions response = callGetPrices(symbol, days);

    JsonNode data = objectMapper.readTree(response.getBody().asString()).get("data");
    data.forEach(node -> {
      assertTrue(node.get("date").textValue().matches("\\d{4}-\\d{2}-\\d{2}"));
      System.out.println(node.get("prices"));
      assertTrue(node.get("prices").get("open").isNumber());
      assertTrue(node.get("prices").get("high").isNumber());
      assertTrue(node.get("prices").get("low").isNumber());
      assertTrue(node.get("prices").get("close").isNumber());
    });
  }

  /**
   * Test that returned JSON from /price has correct data array size when API call is needed.
   */
  @SneakyThrows
  @Test
  public void testGetPricesWithAPICallDataSize_JSON() {
    String symbol = "IBM";
    int days = 5;
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 10);

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(stockPrices);
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(stockPrices, days))
        .willReturn(false);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(any(), eq(days)))
        .willCallRealMethod();
    BDDMockito.given(this.mockStockPriceService.makeApiCall(any(), anyString(), anyString(),
        anyString(), anyString())).willCallRealMethod();
    BDDMockito.given(this.mockStockPriceService.saveStockPricesIfNotExists(any()))
        .willReturn(null);

    ResponseOptions response = callGetPrices(symbol, days);

    JsonNode data = objectMapper.readTree(response.getBody().asString()).get("data");
    assertEquals(days, data.size());
  }

  /**
   * Missing symbol causes Bad Request response and appropriate error message.
   */
  @SneakyThrows
  @Test
  public void testGetPricesMissingSymbol() {
    int days = 5;

    ResponseOptions response = null;
    try {
      response = callGetPrices(pricePath + "?days=" + days);
    } catch (Exception e) {
      System.out.println(e.getClass());
    }

    assertEquals(400, response.getStatusCode());
    JsonNode error = objectMapper.readTree(response.getBody().asString());

    assertEquals("BAD_REQUEST", error.get("status").textValue());
    assertEquals("Required String parameter 'symbol' is not present",
        error.get("message").textValue());
  }

  /**
   * Nonexistent symbol causes 404 response and appropriate error message.
   */
  @SneakyThrows
  @Test
  public void testGetPricesSymbolNotFound() {
    String symbol = "sljsfjlksf";
    int days = 5;

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(new ArrayList<StockPrice>());
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(any(), anyInt()))
        .willReturn(false);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(any(), eq(days)))
        .willCallRealMethod();
    BDDMockito.given(this.mockStockPriceService.makeApiCall(any(), anyString(), anyString(),
        anyString(), anyString())).willCallRealMethod();
    BDDMockito.given(this.mockStockPriceService.saveStockPricesIfNotExists(any()))
        .willReturn(null);

    ResponseOptions response = callGetPrices(symbol, days);
    assertEquals(404, response.getStatusCode());
    JsonNode error = objectMapper.readTree(response.getBody().asString());

    assertEquals("NOT_FOUND", error.get("status").textValue());
    assertEquals("No data could be found for symbol '" + symbol + "'",
        error.get("message").textValue());
  }

  /**
   * Symbol upper/lower case does not affect results.
   */
  @SneakyThrows
  @Test
  public void testGetPricesSymbolCaseDoesNotAffectData() {
    String symbol = "ibm";
    int days = 5;
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 10);

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(stockPrices);
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(stockPrices, days))
        .willReturn(false);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(any(), eq(days)))
        .willCallRealMethod();
    BDDMockito.given(this.mockStockPriceService.makeApiCall(any(), anyString(), anyString(),
        anyString(), anyString())).willCallRealMethod();
    BDDMockito.given(this.mockStockPriceService.saveStockPricesIfNotExists(any()))
        .willReturn(null);

    ResponseOptions responseLower = callGetPrices(symbol.toLowerCase(), days);
    ResponseOptions responseUpper = callGetPrices(symbol.toUpperCase(), days);
    String dataLower = objectMapper.writeValueAsString(
        objectMapper.readTree(responseLower.getBody().asString()).get("data"));
    String dataUpper = objectMapper.writeValueAsString(
        objectMapper.readTree(responseUpper.getBody().asString()).get("data"));

    assertEquals(dataLower, dataUpper);
  }

  /**
   * Missing days uses default value days=20.
   */
  @SneakyThrows
  @Test
  public void testGetPricesMissingDays() {
    String symbol = "IBM";
    List<StockPrice> stockPrices = TestDataGenerator.generateStockPrices(symbol, 30);

    BDDMockito.given(this.mockStockPriceService.findStockPricesBySymbol(symbol))
        .willReturn(stockPrices);
    BDDMockito.given(this.mockStockPriceService.hasNecessaryStockPrices(any(), anyInt()))
        .willReturn(true);
    BDDMockito.given(this.mockStockPriceService.findFirstStockPrices(any(), anyInt()))
        .willCallRealMethod();

    ResponseOptions response = callGetPrices(pricePath + "?symbol=" + symbol);
    JsonNode data = objectMapper.readTree(response.getBody().asString()).get("data");

    assertEquals(20, data.size());
  }

  /**
   * Invalid days format causes Bad Request response and appropriate error message.
   */
  @SneakyThrows
  @Test
  public void testGetPricesInvalidDaysFormat() {
    String symbol = "IBM";
    String days = "abcd";

    ResponseOptions response = callGetPrices(pricePath + "?symbol=" + symbol + "&days=" + days);
    assertEquals(400, response.getStatusCode());
    JsonNode error = objectMapper.readTree(response.getBody().asString());

    assertEquals("BAD_REQUEST", error.get("status").textValue());
    assertEquals("java.lang.NumberFormatException: For input string: '" + days + "'",
        error.get("message").textValue());
  }

  /**
   * Days < 0 causes Bad Request response and appropriate error message.
   */
  @SneakyThrows
  @Test
  public void testGetPricesNegativeDays() {
    String symbol = "IBM";
    int days = -1;

    ResponseOptions response = callGetPrices(symbol, days);

    assertEquals(400, response.getStatusCode());
    JsonNode error = objectMapper.readTree(response.getBody().asString());

    assertEquals("BAD_REQUEST", error.get("status").textValue());
    assertEquals("Days must be greater than or equal to 0. ", error.get("message").textValue());
  }

}
