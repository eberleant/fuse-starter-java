package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.service.StockPriceService;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  /**
   * Test that returned JSON from /price has correct metadata fields and info.
   */
  @Test
  public void testGetPricesMetadata_JSON() {

  }

  /**
   * Test that returned JSON from /price has correct data array size.
   */
  @Test
  public void testGetPricesDataSize_JSON() {

  }

  /**
   * Test that returned JSON from /price has correct fields for each element of data array.
   */
  @Test
  public void testGetPricesDataFields_JSON() {

  }

  /**
   * Test that missing symbol causes Bad Request response and appropriate error message.
   */
  @Test
  public void testGetPricesMissingSymbol() {

  }

  /**
   * Test that nonexistent symbol causes Bad Request response and appropriate error message.
   */
  @Test
  public void testGetPricesSymbolNotFound() {

  }

  /**
   * Test that symbol case does not affect results.
   */
  @Test
  public void testGetPricesSymbolIsCaseInsensitive() {

  }

  /**
   * Test that missing days uses default value days=20.
   */
  @Test
  public void testGetPricesMissingDays() {

  }

  /**
   * Test that invalid days format causes Bad Request response and appropriate error message.
   */
  @Test
  public void testGetPricesInvalidDays() {

  }

}
