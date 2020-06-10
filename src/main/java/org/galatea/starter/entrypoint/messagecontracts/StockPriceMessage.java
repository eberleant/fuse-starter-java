package org.galatea.starter.entrypoint.messagecontracts;

import java.sql.Date;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class StockPriceMessage {
  String symbol;

  Date date;

  StockPriceInfoMessage stockInfo;

}
