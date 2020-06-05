package org.galatea.starter.utils.http.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;

@Slf4j
public class StockPriceSerializer extends StdSerializer<StockPrice> {
  public StockPriceSerializer() {
    this(null);
  }

  private StockPriceSerializer(Class<StockPrice> t) {
    super(t);
  }

  @SneakyThrows
  @Override
  public void serialize(StockPrice value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    jgen.writeStringField("date", value.getDate().toString());
    jgen.writeNumberField("price", value.getPrice());
    jgen.writeEndObject();
  }
}
