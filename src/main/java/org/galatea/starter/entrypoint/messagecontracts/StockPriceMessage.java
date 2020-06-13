package org.galatea.starter.entrypoint.messagecontracts;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class StockPriceMessage {
  String symbol;

  LocalDate date;

  StockPriceInfoMessage stockInfo;

}
