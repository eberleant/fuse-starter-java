package org.galatea.starter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

  @NotEmpty(message = "Symbol must not be empty")
  @NonNull
  private String symbol;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @JsonSerialize(using = LocalDateSerializer.class)
  @NonNull
  private LocalDate date;

  @NonNull
  @Valid
  @Embedded
  private StockPriceInfo prices;

  /**
   * Custom equals method that ignores the value of ID. Returns true if the stock symbols, dates,
   * and prices of the two StockPrice objects are the same, false otherwise.
   * @param obj the StockPrice object to compare with
   * @return
   */
  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof StockPrice)) {
      return false;
    }

    StockPrice otherStockPrice = (StockPrice) obj;
    return this.symbol.equals(otherStockPrice.symbol)
        && this.date.equals(otherStockPrice.date)
        && this.prices.equals(otherStockPrice.prices);
  }

}
