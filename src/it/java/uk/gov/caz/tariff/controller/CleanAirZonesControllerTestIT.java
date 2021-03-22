package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.caz.correlationid.Constants.X_CORRELATION_ID_HEADER;
import static uk.gov.caz.tariff.util.JsonReader.sampleCleanAirZonesJson;
import static uk.gov.caz.tariff.util.JsonReader.sampleTariffJson;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.caz.GlobalExceptionHandlerConfiguration;
import uk.gov.caz.correlationid.Configuration;
import uk.gov.caz.definitions.dto.CleanAirZoneDto;
import uk.gov.caz.definitions.dto.CleanAirZonesDto;
import uk.gov.caz.tariff.dto.InformationUrls;
import uk.gov.caz.tariff.dto.Rates;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.CleanAirZonesRepository;
import uk.gov.caz.tariff.service.TariffRepository;

@ContextConfiguration(classes = {GlobalExceptionHandlerConfiguration.class, Configuration.class,
    CleanAirZonesController.class})
@WebMvcTest
class CleanAirZonesControllerTestIT {

  private static final String SOME_URL = "www.test.uk";

  private static final String CLEAN_AIR_ZONE_ID = "5dd5c926-ed33-4a0a-b911-46324433e866";

  private static final String SOME_CORRELATION_ID = UUID.randomUUID().toString();

  @MockBean
  private TariffRepository tariffRepository;

  @MockBean
  private CleanAirZonesRepository cleanAirZonesRepository;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void missingCorrelationIdShouldResultIn400AndValidMessage() throws Exception {
    given(cleanAirZonesRepository.findAll()).willReturn(prepareCleanAirZones());

    mockMvc.perform(get(CleanAirZonesController.PATH)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Missing request header 'X-Correlation-ID'"));
  }

  @Test
  public void shouldReturnListOfCleanAirZones() throws Exception {
    given(cleanAirZonesRepository.findAll()).willReturn(prepareCleanAirZones());

    mockMvc.perform(get(CleanAirZonesController.PATH)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .header(X_CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isOk())
        .andExpect(content().json(sampleCleanAirZonesJson()));
  }

  @Test
  public void shouldReturnTariffAndStatusOk() throws Exception {
    given(tariffRepository.findByCleanAirZoneId(UUID.fromString(CLEAN_AIR_ZONE_ID)))
        .willReturn(buildTariff());

    mockMvc.perform(get(tariffWithCleanAirZoneId(CLEAN_AIR_ZONE_ID))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .header(X_CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(content().json(sampleTariffJson()))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnNotFoundWhenTariffNotExist() throws Exception {
    mockMvc.perform(get(tariffWithCleanAirZoneId(CLEAN_AIR_ZONE_ID))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .header(X_CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldReturnNotFoundWhenInvalidUUID() throws Exception {
    mockMvc.perform(get(tariffWithCleanAirZoneId("asd"))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .header(X_CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isNotFound());
  }

  private static String tariffWithCleanAirZoneId(String cleanAirZoneId) {
    return CleanAirZonesController.PATH + "/" + cleanAirZoneId + "/tariff";
  }

  private Optional<Tariff> buildTariff() {
    InformationUrls informationUrls = InformationUrls.builder()
        .becomeCompliant(SOME_URL)
        .boundary(SOME_URL)
        .exemptionOrDiscount(SOME_URL)
        .mainInfo(SOME_URL)
        .paymentsCompliance(SOME_URL)
        .privacyPolicy(SOME_URL)
        .fleetsCompliance(SOME_URL)
        .publicTransportOptions(SOME_URL)
        .build();
    Rates rates = Rates.builder()
        .bus(new BigDecimal("5.50"))
        .car(new BigDecimal("15.50"))
        .miniBus(new BigDecimal("25.00"))
        .coach(new BigDecimal("15.60"))
        .hgv(new BigDecimal("5.69"))
        .moped(new BigDecimal("49.49"))
        .motorcycle(new BigDecimal("80.01"))
        .phv(new BigDecimal("80.10"))
        .van(new BigDecimal("80.00"))
        .taxi(new BigDecimal("2.00"))
        .build();

    return Optional.ofNullable(Tariff.builder()
        .cleanAirZoneId(UUID.fromString(CLEAN_AIR_ZONE_ID))
        .name("Bath")
        .tariffClass('C')
        .chargeIdentifier("BAT01")
        .activeChargeStartDate("2021-03-15")
        .activeChargeStartDateText("15 March 2021")
        .displayFrom("2020-01-01")
        .displayOrder(1)
        .informationUrls(informationUrls)
        .rates(rates)
        .build());
  }

  private CleanAirZonesDto prepareCleanAirZones() {
    return CleanAirZonesDto.builder().cleanAirZones(
        newArrayList(
            caz("Birmingham", "0d7ab5c4-5fff-4935-8c4e-56267c0c9493",
                "https://www.birmingham.gov.uk/info/20076/pollution/"
                    + "1763/a_clean_air_zone_for_birmingham/3",
                "https://exemption.birmingham.gov.uk",
                "https://www.birmingham.gov.uk/info/20015/environment/2005/privacy_statement_-_environmental_health/2",
                LocalDate.of(2021, 6, 1),
                "1 June 2021",
                LocalDate.of(2021, 1, 1), 2,
                "Birmingham City Council"),

            caz("Bath", "5dd5c926-ed33-4a0a-b911-46324433e866",
                "http://www.bathnes.gov.uk/zonemaps",
                "http://www.bathnes.gov.uk/CAZexemptions",
                "https://beta.bathnes.gov.uk/council-privacy-policy",
                LocalDate.of(2021, 3, 15),
                "15 March 2021",
                LocalDate.of(2021, 1, 1), 1,
                "Bath and North East Somerset Council")
        )).build();
  }

  private CleanAirZoneDto caz(String cazName, String cleanAirZoneId, String boundaryUrl,
      String exemptionUrl, String privacyPolicyUrl, LocalDate activeChargeStartDate, String activeChargeStartDateText,
      LocalDate displayFrom, Integer displayOrder,String operatorName) {
    return CleanAirZoneDto.builder()
        .name(cazName)
        .cleanAirZoneId(UUID.fromString(cleanAirZoneId))
        .boundaryUrl(URI.create(boundaryUrl))
        .exemptionUrl(URI.create(exemptionUrl))
        .privacyPolicyUrl(URI.create(privacyPolicyUrl))
        .activeChargeStartDate(activeChargeStartDate.format(DateTimeFormatter.ISO_DATE))
        .activeChargeStartDateText(activeChargeStartDateText)
        .displayFrom(displayFrom.format(DateTimeFormatter.ISO_DATE))
        .displayOrder(displayOrder)
        .operatorName(operatorName)
        .build();
  }
}