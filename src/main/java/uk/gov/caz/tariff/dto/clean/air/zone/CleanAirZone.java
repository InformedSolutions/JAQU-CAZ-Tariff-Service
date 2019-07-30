package uk.gov.caz.tariff.dto.clean.air.zone;

import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * Value object that holds details of one Clean Air Zone (CAZ).
 */
@Value
@Builder
public class CleanAirZone {

  @ApiModelProperty(value = "${swagger.model.descriptions.cleanAirZone.cleanAirZoneId}")
  @NotNull
  UUID cleanAirZoneId;

  @ApiModelProperty(value = "${swagger.model.descriptions.cleanAirZone.name}")
  @NotNull
  @Size(min = 1, max = 60)
  String name;

  @ApiModelProperty(value = "${swagger.model.descriptions.cleanAirZone.boundaryUrl}")
  URI boundaryUrl;
}

