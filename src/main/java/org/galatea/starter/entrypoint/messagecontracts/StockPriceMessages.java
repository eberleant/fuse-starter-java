package org.galatea.starter.entrypoint.messagecontracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.galatea.starter.utils.Helpers;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceMessages {
  List<StockPriceMessage> data;

  @JsonCreator
  private StockPriceMessages(
      @JsonProperty("Meta Data") Map<String, String> metadata,
      @JsonProperty("Time Series (Daily)") Map<String, StockPriceInfoMessage> timeSeries) {
    String symbol = metadata.get("2. Symbol");
    this.data = extractData(symbol, timeSeries);
  }

  private List<StockPriceMessage> extractData(String symbol,
      Map<String, StockPriceInfoMessage> timeSeries) {
    List<StockPriceMessage> data = new ArrayList<>();
    timeSeries.forEach((date, info) -> data.add(StockPriceMessage.builder()
        .symbol(symbol)
        .date(Helpers.stringToDate(date))
        .stockInfo(info).build()));
    return data;
  }
}
