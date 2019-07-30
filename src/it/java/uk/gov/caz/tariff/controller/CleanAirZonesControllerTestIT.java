package uk.gov.caz.tariff.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.caz.tariff.dto.tariff.InformationUrls;
import uk.gov.caz.tariff.dto.tariff.Rates;
import uk.gov.caz.tariff.dto.tariff.Tariff;
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

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldReturnListOfCleanAirZones() throws Exception {
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
        .andExpect(jsonPath("$.cleanAirZoneId").value(CLEAN_AIR_ZONE_ID.toString()))
        .andExpect(jsonPath("$.name").value("Leeds"))
        .andExpect(jsonPath("$.tariffClass").value("C"))
        .andExpect(jsonPath("$.motorcyclesChargeable").value("false"))
        .andExpect(jsonPath("$.rates.bus").value(ONE))
        .andExpect(jsonPath("$.rates.car").value(ONE))
        .andExpect(jsonPath("$.rates.coach").value(ONE))
        .andExpect(jsonPath("$.rates.hgv").value(ONE))
        .andExpect(jsonPath("$.rates.largeVan").value(ONE))
        .andExpect(jsonPath("$.rates.miniBus").value(ONE))
        .andExpect(jsonPath("$.rates.moped").value(ONE))
        .andExpect(jsonPath("$.rates.motorcycle").value(ONE))
        .andExpect(jsonPath("$.rates.phv").value(ONE))
        .andExpect(jsonPath("$.rates.smallVan").value(ONE))
        .andExpect(jsonPath("$.rates.taxi").value(ONE))
        .andExpect(jsonPath("$.informationUrls.becomeCompliant").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.boundary").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.emissionsStandards").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.exemptionOrDiscount").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.hoursOfOperation").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.payCaz").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.pricing").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.mainInfo").value(SOME_URL))
        .andExpect(jsonPath("$.informationUrls.financialAssistance").value(SOME_URL))
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
        .bus(BigDecimal.ONE)
        .car(BigDecimal.ONE)
        .coach(BigDecimal.ONE)
        .hgv(BigDecimal.ONE)
        .largeVan(BigDecimal.ONE)
        .miniBus(BigDecimal.ONE)
        .moped(BigDecimal.ONE)
        .motorcycle(BigDecimal.ONE)
        .phv(BigDecimal.ONE)
        .smallVan(BigDecimal.ONE)
        .taxi(BigDecimal.ONE)
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
}