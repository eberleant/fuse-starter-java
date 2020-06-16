package org.galatea.starter.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.rpsy.IStockPriceRpsy;
import org.galatea.starter.service.StockPriceService;
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
  IStockPriceRpsy stockPriceRpsy;

  @Value("${alpha-vantage.api-key}")
  private String apiKey;

  @Value("${alpha-vantage.dailyTimeSeriesPath}")
  private String basePath;

  ObjectMapper objectMapper = new ObjectMapper();

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
      @RequestParam(value = "symbol") final String symbol,
      @RequestParam(value = "days", defaultValue = "20") @Min(value = 0,
          message = "Days must be greater than or equal to 0") final int days,
      @RequestParam(value = "requestId", required = false) final String requestId) {

    // if an external request id was provided, grab it
    processRequestId(requestId);

    // get list of StockPrice objects to return
    List<StockPrice> stockPrices = stockPriceService.getStockPrices(symbol, days, apiKey, basePath);

    // create metadata object
    ObjectNode metadata = getMetadata(symbol, days);

    // create and return final json with metadata + StockPrice array
    ObjectNode rootNode = objectMapper.createObjectNode();
    rootNode.set("metadata", metadata);
    rootNode.set("data", objectMapper.readTree(objectMapper.writeValueAsString(stockPrices)));
    return rootNode;
  }

  /**
   * Get an ObjectNode representing the metadata of the request.
   * @param symbol from params in API request
   * @param days from params in API request; represents number of days to retrieve stock info
   * @return
   */
  private ObjectNode getMetadata(final String symbol, final int days) {
    ObjectNode metadata = objectMapper.createObjectNode();
    metadata.put("description", "Daily stock prices (open)"); // should i use open or?
    metadata.put("symbol", symbol);
    metadata.put("days", days);
    metadata.put("timezone", "America/New_York");
    return metadata;
  }
}
