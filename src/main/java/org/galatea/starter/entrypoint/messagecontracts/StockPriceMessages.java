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
import org.galatea.starter.utils.Helpers;
import org.galatea.starter.utils.translation.TranslationException;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // for builder
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceMessages {
  List<StockPriceMessage> data;

  /**
   * Custom JSON de-serialization method. Exceptions in this process throw an
   * InvalidDefinitionException by Jackson.
   * @param metadata
   * @param timeSeries
   */
  @JsonCreator
  private StockPriceMessages(
    @JsonProperty("Meta Data") final Map<String, String> metadata,
    @JsonProperty("Time Series (Daily)") final Map<String, StockPriceInfoMessage> timeSeries) {
    String symbol = metadata.get("2. Symbol");
    assert symbol != null;
    this.data = extractData(symbol, timeSeries);
  }

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
