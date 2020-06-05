package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.service.StockPriceService;
import org.galatea.starter.utils.Helpers;
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
public class StockPriceController extends BaseRestController {

  @Autowired
  StockPriceService stockPriceService;

  @Autowired
  ObjectMapper mapper;

  @Value("${alpha-vantage.api-key}")
  private String apiKey;

  @Value("${alpha-vantage.basePath}")
  private String basePath;

  @Value("${alpha-vantage.price-key}")
  private String priceKey;

  @Value("${alpha-vantage.daily-time-series-key}")
  private String dailyTimeSeriesKey;

  /**
   * Handle /price route.
   * @param stock symbol from parameters of /price URL
   * @param days from parameters of /price URL
   * @return StockPrice object
   */
  @SneakyThrows
  @GetMapping(value = "${mvc.getPricePath}", produces = {
      MediaType.APPLICATION_JSON_VALUE})
  public ObjectNode price(
      @RequestParam(value = "stock", defaultValue = "DNKN") final String stock,
      @RequestParam(value = "days", defaultValue = "5") @Min(0) final int days,
      @RequestParam(value = "requestId", required = false) final String requestId) {
    // if an external request id was provided, grab it
    processRequestId(requestId);
    // make db call

    // check if making api call is necessary

    // make api call to AlphaVantage
    URL requestUrl = new URL(basePath + "/query?function=TIME_SERIES_DAILY"
        + "&symbol=" + stock
        + "&outputsize=" + (days > 100 ? "full" : "compact")
        + "&apikey=" + apiKey);
    JsonNode avResponseJson = mapper.readTree(requestUrl);

    // create array of StockPrice objects from db/api calls
    List<StockPrice> stockPrices =
        stockPriceService.findMostRecentStockPrices(days,
            createStockPrices(avResponseJson.get(dailyTimeSeriesKey), stock));


    // store result of api call in db

    ObjectNode metadata = getMetadata(stock, days);
    // create and return final json with audit info + StockPrice array
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.set("metadata", metadata);
    rootNode.set("data", mapper.valueToTree(stockPrices));
    return rootNode;
  }

  /**
   * Helper method for getting an ObjectNode representing the audit info of the request.
   * @param stock symbol from params in API request
   * @param days from params in API request; represents number of days to retrieve stock info
   * @return
   */
  private ObjectNode getMetadata(final String stock, final int days) {
    ObjectNode metadata = mapper.createObjectNode();
    metadata.put("description", "Daily stock prices (open)"); // should i use open or?
    metadata.put("stock", stock);
    metadata.put("days", days);
    metadata.put("request-date",
        Helpers.getStartOfDay(new Date(System.currentTimeMillis())).toString());
    metadata.put("time-zone", "US/Eastern");
    return metadata;
  }

  @SneakyThrows
  private ArrayList<StockPrice> createStockPrices(
      JsonNode timeSeries, final String stock) {
    ArrayList<StockPrice> stockPrices = new ArrayList<>();
    Iterator<Entry<String, JsonNode>> timeSeriesNodes = timeSeries.fields();
    // build a StockPrice object for each entry in timeSeries
    while (timeSeriesNodes.hasNext()) {
      Map.Entry<String, JsonNode> timeSeriesEntry = timeSeriesNodes.next();
      // switch to using custom constructor?
      // use custom ITranslator??
      stockPrices.add(
          StockPrice.builder()
            .date(Helpers.stringToDate(timeSeriesEntry.getKey()))
            .price(new BigDecimal(timeSeriesEntry.getValue().get(priceKey).textValue()))
            .stock(stock).build());
    }

    return stockPrices;
  }
}
