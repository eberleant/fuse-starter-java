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
  @DecimalMin("0.00")
  @NonNull
  private BigDecimal open;

  @DecimalMin("0.00")
  @NonNull
  private BigDecimal high;

  @DecimalMin("0.00")
  @NonNull
  private BigDecimal low;

  @DecimalMin("0.00")
  @NonNull
  private BigDecimal close;
}
