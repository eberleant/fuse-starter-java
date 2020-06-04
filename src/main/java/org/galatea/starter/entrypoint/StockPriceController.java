package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicLong;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
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
      @RequestParam(value = "stock", defaultValue = "DNDK") final String stock,
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
    StockPrice[] stockPrices = new StockPrice[days];
    stockPrices[0] = StockPrice.builder()
        .stock(stock)
        .date(new Date(0))
        .price(new BigDecimal(0)).build();

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
}
