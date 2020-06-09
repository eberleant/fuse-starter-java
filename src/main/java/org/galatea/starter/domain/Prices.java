package org.galatea.starter.domain;

import java.math.BigDecimal;
import javax.persistence.Embeddable;
import javax.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@NoArgsConstructor(access = AccessLevel.PRIVATE) // JPA
@Builder
@Data
@Embeddable
public class Prices {
  @DecimalMin(value = "0.00", message = "Open price must be greater than or equal to 0.00")
  @NonNull
  private BigDecimal open;

  @DecimalMin(value = "0.00", message = "High price must be greater than or equal to 0.00")
  @NonNull
  private BigDecimal high;

  @DecimalMin(value = "0.00", message = "Low price must be greater than or equal to 0.00")
  @NonNull
  private BigDecimal low;

  @DecimalMin(value = "0.00", message = "Close price must be greater than or equal to 0.00")
  @NonNull
  private BigDecimal close;
}
