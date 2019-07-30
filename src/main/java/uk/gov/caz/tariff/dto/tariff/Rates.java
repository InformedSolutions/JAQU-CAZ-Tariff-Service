package uk.gov.caz.tariff.dto.tariff;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

/**
 * Value object that holds rates.
 */
@Value
@Builder
public class Rates {

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.bus}")
  BigDecimal bus;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.coach}")
  BigDecimal coach;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.taxi}")
  BigDecimal taxi;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.phv}")
  BigDecimal phv;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.hgv}")
  BigDecimal hgv;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.largeVan}")
  BigDecimal largeVan;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.miniBus}")
  BigDecimal miniBus;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.smallVan}")
  BigDecimal smallVan;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.car}")
  BigDecimal car;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.motorcycle}")
  BigDecimal motorcycle;

  @ApiModelProperty(value = "${swagger.model.descriptions.rate.moped}")
  BigDecimal moped;
}
