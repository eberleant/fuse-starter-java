package org.galatea.starter.entrypoint.messagecontracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.Helpers;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for builder
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceMessages {
  List<StockPriceMessage> data;

  /**
   * Custom JSON de-serialization method. Exceptions in this process throw an
   * InvalidDefinitionException by Jackson.
   * @param metadata metadata from Alpha Vantage's API response
   * @param timeSeries time series information from Alpha Vantage's API response
   */
  @JsonCreator
  private StockPriceMessages(
      @JsonProperty("Meta Data") final Map<String, String> metadata,
      @JsonProperty("Time Series (Daily)") final Map<String, StockPriceInfoMessage> timeSeries) {
    String symbol = metadata.get("2. Symbol");
    this.data = extractData(symbol, timeSeries);
  }

  /**
   * For each element of Alpha Vantage's time series information, create a StockPriceMessage object.
   * @param symbol stock symbol
   * @param timeSeries partially (auto)de-serialized time series info from Alpha Vantage
   * @return
   */
  private List<StockPriceMessage> extractData(final String symbol,
      final Map<String, StockPriceInfoMessage> timeSeries) {
    List<StockPriceMessage> data = new ArrayList<>();
    timeSeries.forEach((date, info) -> data.add(StockPriceMessage.builder()
        .symbol(symbol)
        .date(Helpers.stringToDate(date))
        .stockInfo(info).build()));
    return data;
  }
}
