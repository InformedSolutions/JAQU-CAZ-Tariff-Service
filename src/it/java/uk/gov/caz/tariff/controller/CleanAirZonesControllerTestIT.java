package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;
import uk.gov.caz.tariff.dto.InformationUrls;
import uk.gov.caz.tariff.dto.Rates;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.CleanAirZonesRepository;
import uk.gov.caz.tariff.service.TariffRepository;

@WebMvcTest(CleanAirZonesController.class)
class CleanAirZonesControllerTestIT {

  private static final String SOME_URL = "www.test.uk";

  private static final String ONE = "1";

  private static final String SOME_CORRELATION_ID = "63be7528-7efd-4f31-ae68-11a6b709ff1c";

  private static final UUID CLEAN_AIR_ZONE_ID = UUID
      .fromString("8ed3580b-f155-4f6d-ab12-5a96b071a0a7");

  private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

  private static final String GET_TARIFF_PATH =
      CleanAirZonesController.PATH + "/" + CLEAN_AIR_ZONE_ID + "/tariff";

  @MockBean
  private TariffRepository tariffRepository;

  @MockBean
  private CleanAirZonesRepository cleanAirZonesRepository;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldReturnListOfCleanAirZones() throws Exception {
    given(cleanAirZonesRepository.findAll()).willReturn(prepareCleanAirZones());

    mockMvc.perform(get(CleanAirZonesController.PATH)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cleanAirZones[0].cleanAirZoneId")
            .value("42395f51-e924-42b4-8585-b1749dc05bfc"))
        .andExpect(jsonPath("$.cleanAirZones[0].name").value("Birmingham"))
        .andExpect(jsonPath("$.cleanAirZones[0].boundaryUrl")
            .value("https://www.birmingham.gov.uk/info/20076/pollution/"
                + "1763/a_clean_air_zone_for_birmingham/3)"))
        .andExpect(jsonPath("$.cleanAirZones[1].cleanAirZoneId")
            .value("146bbfd3-1928-41d3-9575-5f9e58e61ee1"))
        .andExpect(jsonPath("$.cleanAirZones[1].name").value("Leeds"))
        .andExpect(jsonPath("$.cleanAirZones[1].boundaryUrl")
            .value("https://www.arcgis.com/home/webmap/viewer.html?webmap="
                + "de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621"))
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnTariffAndStatusOk() throws Exception {
    given(tariffRepository.findByCleanAirZoneId(CLEAN_AIR_ZONE_ID)).willReturn(buildTariff());

    mockMvc.perform(get(GET_TARIFF_PATH)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(content().json(readTariffJson()))
        .andExpect(status().isOk())
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnNotFoundWhenTariffNotExist() throws Exception {
    mockMvc.perform(get(GET_TARIFF_PATH)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isNotFound())
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  private Optional<Tariff> buildTariff() {
    InformationUrls informationUrls = InformationUrls.builder()
        .becomeCompliant(SOME_URL)
        .boundary(SOME_URL)
        .emissionsStandards(SOME_URL)
        .exemptionOrDiscount(SOME_URL)
        .hoursOfOperation(SOME_URL)
        .payCaz(SOME_URL)
        .pricing(SOME_URL)
        .mainInfo(SOME_URL)
        .financialAssistance(SOME_URL)
        .build();
    Rates rates = Rates.builder()
        .bus(new BigDecimal("5.50"))
        .car(new BigDecimal("50.00"))
        .coach(new BigDecimal("15.60"))
        .hgv(new BigDecimal("5.69"))
        .largeVan(new BigDecimal("100.00"))
        .miniBus(new BigDecimal("25.50"))
        .moped(new BigDecimal("49.49"))
        .motorcycle(new BigDecimal("80.01"))
        .phv(new BigDecimal("80.10"))
        .smallVan(new BigDecimal("80.00"))
        .taxi(new BigDecimal("2.00"))
        .build();

    return Optional.ofNullable(Tariff.builder()
        .cleanAirZoneId(CLEAN_AIR_ZONE_ID)
        .name("Leeds")
        .motorcyclesChargeable(false)
        .tariffClass('C')
        .informationUrls(informationUrls)
        .rates(rates)
        .build());
  }

  private CleanAirZones prepareCleanAirZones() {
    return new CleanAirZones(
        newArrayList(
            caz("Birmingham", UUID.fromString("42395f51-e924-42b4-8585-b1749dc05bfc"),
                "https://www.birmingham.gov.uk/info/20076/pollution/"
                    + "1763/a_clean_air_zone_for_birmingham/3)"),

            caz("Leeds", UUID.fromString("146bbfd3-1928-41d3-9575-5f9e58e61ee1"),
                "https://www.arcgis.com/home/webmap/viewer.html?webmap="
                    + "de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621")
        ));
  }

  private CleanAirZone caz(String cazName, UUID cazId, String boundaryUrl) {
    return CleanAirZone.builder()
        .name(cazName)
        .cleanAirZoneId(cazId)
        .boundaryUrl(URI.create(boundaryUrl))
        .build();
  }

  private String readTariffJson() throws IOException {
    return Resources.toString(Resources.getResource("data/json/tariff.json"), Charsets.UTF_8);
  }
}