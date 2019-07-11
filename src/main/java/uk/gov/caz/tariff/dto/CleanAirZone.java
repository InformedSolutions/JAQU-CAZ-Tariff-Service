package uk.gov.caz.tariff.dto;

import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
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

  @ApiModelProperty(value = "${swagger.model.descriptions.cleanAirZone.name}")
  @NotNull
  @Size(min = 1, max = 50)
  String name;

  @ApiModelProperty(value = "${swagger.model.descriptions.cleanAirZone.id}")
  @NotNull
  String id;

  @ApiModelProperty(value = "${swagger.model.descriptions.cleanAirZone.infoUrl}")
  @NotNull
  URI infoUrl;
}

