package uk.gov.caz.tariff.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

/**
 * Value object that holds information urls.
 */
@Value
@Builder
public class InformationUrls {

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.mainInfoUrl}")
  String mainInfo;

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.exemptionOrDiscountUrl}")
  String exemptionOrDiscount;

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.becomeCompliantUrl}")
  String becomeCompliant;

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.boundaryUrl}")
  String boundary;

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.paymentsComplianceUrl}")
  String paymentsCompliance;

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.fleetsComplianceUrl}")
  String fleetsCompliance;

  @ApiModelProperty(value = "${swagger.model.descriptions.informationUrl.privacyPolicyUrl}")
  String privacyPolicy;

  @ApiModelProperty(value =
      "${swagger.model.descriptions.informationUrl.publicTransportOptionsUrl}")
  String publicTransportOptions;
}