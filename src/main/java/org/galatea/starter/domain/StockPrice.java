package org.galatea.starter.domain;

import java.math.BigDecimal;
import java.sql.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Builder
// immutable version of @Data; creates getters and constructor (@AllArgsConstructor)
// may change to @Data later if decide it should be mutable?
@Value
@Slf4j // creates logger object, log
public class StockPrice {

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
