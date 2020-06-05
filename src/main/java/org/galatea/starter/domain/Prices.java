package org.galatea.starter.domain;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE) // For builder
@Builder
@Value
public class Prices {
  @DecimalMin("0.00")
  @NonNull
  BigDecimal open;

  @DecimalMin("0.00")
  @NonNull
  BigDecimal high;

  @DecimalMin("0.00")
  @NonNull
  BigDecimal low;

  @DecimalMin("0.00")
  @NonNull
  BigDecimal close;
}
