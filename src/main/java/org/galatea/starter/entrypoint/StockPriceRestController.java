package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.service.StockPriceService;
import org.galatea.starter.utils.Helpers;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j // logging
@Validated // evaluate method parameter constraint annotations
@RestController // @Controller + @ResponseBody
public class StockPriceRestController extends BaseRestController {

  @Autowired
  StockPriceService stockPriceService;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ITranslator<JsonNode, List<StockPrice>> timeSeriesJsonTranslator;

  @Value("${alpha-vantage.api-key}")
  private String apiKey;

  @Value("${alpha-vantage.basePath}")
  private String basePath;

  @Value("${alpha-vantage.daily-time-series-key}")
  private String dailyTimeSeriesKey;

  @Value("${alpha-vantage.open-price-key}")
  private String openPriceKey;

  @Value("${alpha-vantage.high-price-key}")
  private String highPriceKey;

  @Value("${alpha-vantage.low-price-key}")
  private String lowPriceKey;

  @Value("${alpha-vantage.close-price-key}")
  private String closePriceKey;

  /**
   * Handle /price route.
   * @param symbol from parameters of /price URL
   * @param days from parameters of /price URL
   * @return StockPrice object
   */
  @SneakyThrows
  @GetMapping(value = "${mvc.getPricePath}", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public ObjectNode price(
      @RequestParam(value = "symbol", defaultValue = "DNKN") final String symbol,
      @RequestParam(value = "days", defaultValue = "5") @Min(0) final int days,
      @RequestParam(value = "requestId", required = false) final String requestId) {
    // if an external request id was provided, grab it
    processRequestId(requestId);
    // make db call
    List<StockPrice> stockPrices = stockPriceService.findStockPricesBySymbol(symbol);
    // make api call to AlphaVantage if necessary
    if (!stockPriceService.hasNecessaryStockPrices(stockPrices, days)) {
      stockPrices = timeSeriesJsonTranslator.translate(stockPriceService.makeApiCall(
          objectMapper, apiKey, basePath, symbol, (days > 100 ? "full" : "compact")));
      // store result of api call in db
      stockPriceService.saveStockPricesIfNotExists(stockPrices);
    }
    // filter to only necessary stock prices
    stockPrices = stockPriceService.findFirstStockPrices(days, stockPrices);
    // create metadata object
    ObjectNode metadata = getMetadata(symbol, days);
    // create and return final json with metadata + StockPrice array
    ObjectNode rootNode = objectMapper.createObjectNode();
    rootNode.set("metadata", metadata);
    rootNode.set("data", objectMapper.readTree(objectMapper.writeValueAsString(stockPrices)));
    return rootNode;
  }

  /**
   * Helper method for getting an ObjectNode representing the audit info of the request.
   * @param symbol from params in API request
   * @param days from params in API request; represents number of days to retrieve stock info
   * @return
   */
  private ObjectNode getMetadata(final String symbol, final int days) {
    ObjectNode metadata = objectMapper.createObjectNode();
    metadata.put("description", "Daily stock prices (open)"); // should i use open or?
    metadata.put("stock", symbol);
    metadata.put("days", days);
    metadata.put("request-date", Helpers.getDateNDaysAgo(0).toString());
    metadata.put("time-zone", "US/Eastern");
    return metadata;
  }
}
