package org.galatea.starter.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // JPA
@Builder
@Data
@Embeddable
public class StockPriceInfo {
  @DecimalMin(value = "0.00", message = "Open price must be greater than or equal to 0.00")
  @NonNull
  @Column(precision = 12, scale = 4)
  private BigDecimal open;

  @DecimalMin(value = "0.00", message = "High price must be greater than or equal to 0.00")
  @NonNull
  @Column(precision = 12, scale = 4)
  private BigDecimal high;

  @DecimalMin(value = "0.00", message = "Low price must be greater than or equal to 0.00")
  @NonNull
  @Column(precision = 12, scale = 4)
  private BigDecimal low;

  @DecimalMin(value = "0.00", message = "Close price must be greater than or equal to 0.00")
  @NonNull
  @Column(precision = 12, scale = 4)
  private BigDecimal close;

  @Min(value = 0, message = "Volume must be greater than or equal to 0")
  @NonNull
  private long volume;
}
