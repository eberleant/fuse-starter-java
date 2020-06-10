package org.galatea.starter.entrypoint.messagecontracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
public class StockPriceInfoMessage {
  @JsonProperty("1. open")
  private BigDecimal open;

  @JsonProperty("2. high")
  private BigDecimal high;

  @JsonProperty("3. low")
  private BigDecimal low;

  @JsonProperty("4. close")
  private BigDecimal close;

  @JsonProperty("5. volume")
  int volume;
}
