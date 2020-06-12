package org.galatea.starter.entrypoint.messagecontracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // for Jackson
@AllArgsConstructor(access = AccessLevel.PRIVATE) // for builder
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPriceInfoMessage {
  @JsonProperty("1. open")
  private BigDecimal open;

  @JsonProperty("2. high")
  private BigDecimal high;

  @JsonProperty(value = "3. low", defaultValue = "0")
  private BigDecimal low;

  @JsonProperty("4. close")
  private BigDecimal close;

  @JsonProperty("5. volume")
  private int volume;
}
