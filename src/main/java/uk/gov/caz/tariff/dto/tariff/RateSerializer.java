package uk.gov.caz.tariff.dto.tariff;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;

public class RateSerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeNumber(value.stripTrailingZeros().toPlainString());
  }
}