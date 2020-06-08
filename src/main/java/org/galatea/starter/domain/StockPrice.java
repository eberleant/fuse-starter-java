package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.sql.Date;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // For JPA
@Builder
// may consider changing to @Value (changed to @Data bc JPA needs no args constructor)
@Data
@Slf4j // creates logger object, log
@JsonIgnoreProperties({"id", "symbol"})
@Entity
public class StockPrice {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NonNull
  private String symbol;

  @NonNull
  private Date date;

  @NonNull
  @Embedded
  private Prices prices;

  public boolean equals(StockPrice other) {
    return this.symbol.equals(other.symbol)
        && this.date.equals(other.date)
        && this.prices.equals(other.prices);
  }

}
