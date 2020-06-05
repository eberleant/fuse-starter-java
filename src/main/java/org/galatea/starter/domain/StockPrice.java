package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.sql.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@Builder
// may consider changing to @Data (@Value does not include setters)
@Value
@Slf4j // creates logger object, log
@JsonIgnoreProperties({"id", "stock"})
public class StockPrice {
//  public StockPrice(Map.Entry<String, JsonNode> timeSeriesEntry, final String stock, String priceKey) {
//    // consider calling AllArgsConstructor instead of setting fields manually
//    this.date = stringToDate(timeSeriesEntry.getKey());
//    this.stock = stock;
//    this.price = new BigDecimal(mapper.writeValueAsString(
//        timeSeriesEntry.getValue().get(priceKey)));
//  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  @NonNull
  String stock;

  @NonNull
  Date date;

  @NonNull
  Prices prices;

}
