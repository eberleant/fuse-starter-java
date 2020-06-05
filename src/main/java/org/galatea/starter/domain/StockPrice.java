package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.sql.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.utils.http.converter.StockPriceSerializer;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@Builder
// may consider changing to @Value and having @NonFinal on id? check if mutable using builder/setter
@Data
@Slf4j // creates logger object, log
@JsonSerialize(using = StockPriceSerializer.class)
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

  @DecimalMin("0.00")
  @NonNull
  BigDecimal price;

  @NonNull
  Date date;

}
