package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
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
        + "&contentsize=" + (days > 100 ? "full" : "compact")
        + "&apikey=" + apiKey);
    JsonNode avResponseJson = mapper.readTree(requestUrl);

    // create array of StockPrice objects from db/api calls
    System.out.println(dailyTimeSeriesKey);
    System.out.println(mapper.writeValueAsString(avResponseJson.get(dailyTimeSeriesKey)));

    ArrayList<StockPrice> stockPrices = createStockPrices(
        avResponseJson.get(dailyTimeSeriesKey), stock);

    // store result of api call in db

    // get audit info (as ObjectNode? HashMap?)
    ObjectNode auditInfo = getAuditInfo(stock, days);
    // create and return final json with audit info + StockPrice array
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.set("audit-info", auditInfo);
    rootNode.set("data", mapper.valueToTree(stockPrices));
    return rootNode;
  }

  /**
   * Helper method for getting an ObjectNode representing the audit info of the request.
   * @param stock symbol from params in API request
   * @param days from params in API request; represents number of days to retrieve stock info
   * @return
   */
  private ObjectNode getAuditInfo(final String stock, final int days) {
    ObjectNode auditInfo = mapper.createObjectNode();
    auditInfo.put("description", "Daily stock prices (open)"); // should i use open or?
    auditInfo.put("stock", stock);
    auditInfo.put("days", days);
    auditInfo.put("request-date", new Date(System.currentTimeMillis()).toString());
    auditInfo.put("time-zone", "US/Eastern");
    return auditInfo;
  }

  @SneakyThrows
  private ArrayList<StockPrice> createStockPrices(
      JsonNode timeSeries, final String stock) {
    System.out.println(timeSeries.fieldNames());
    ArrayList<StockPrice> stockPrices = new ArrayList<>();
    Iterator<Entry<String, JsonNode>> timeSeriesNodes = timeSeries.fields();
    while (timeSeriesNodes.hasNext()) {
      Map.Entry<String, JsonNode> timeSeriesEntry = timeSeriesNodes.next();
      System.out.println(timeSeriesEntry.getValue().get(priceKey).textValue());
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
