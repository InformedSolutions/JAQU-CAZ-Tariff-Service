package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.caz.tariff.util.Constants.CORRELATION_ID_HEADER;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;
import uk.gov.caz.tariff.dto.InformationUrls;
import uk.gov.caz.tariff.dto.Rates;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.CleanAirZonesRepository;
import uk.gov.caz.tariff.service.TariffRepository;

@ExtendWith(MockitoExtension.class)
class CleanAirZonesControllerTest {

  private static final Integer SOME_CHARGE_DEFINITION_ID = 5;

  private static final String SOME_URL = "www.test.uk";

  @Mock
  private TariffRepository tariffRepository;

  @Mock
  private CleanAirZonesRepository cleanAirZonesRepository;

  @InjectMocks
  private CleanAirZonesController cleanAirZonesController;

  @Test
  public void shouldReturnCleanAirZones() {
    // given
    when(cleanAirZonesRepository.findAll()).thenReturn(prepareCleanAirZones());

    // when
    ResponseEntity<CleanAirZones> cleanAirZones = cleanAirZonesController
        .cleanAirZones("correlation-cleanAirZoneId");

    // then
    assertThat(cleanAirZones).isNotNull();
    assertThat(cleanAirZones.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(cleanAirZones.getBody().getCleanAirZones()).contains(
        CleanAirZone.builder()
            .cleanAirZoneId(4)
            .name("A")
            .boundaryUrl(URI.create(
                "https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham/3"))
            .build()
    );
  }

  @Test
  public void shouldReturnIncomingXCorrelationId() {
    // given

    // when
    ResponseEntity<CleanAirZones> cleanAirZones = cleanAirZonesController
        .cleanAirZones("correlation-cleanAirZoneId");

    // then
    assertThat(cleanAirZones).isNotNull();
    assertThat(cleanAirZones.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(cleanAirZones.getHeaders())
        .containsEntry(CORRELATION_ID_HEADER, newArrayList("correlation-cleanAirZoneId"));
  }

  @Test
  public void shouldReturnSomeTariff() {
    // given
    when(tariffRepository.findByCleanAirZoneId(SOME_CHARGE_DEFINITION_ID)).thenReturn(prepareTariff());

    // when
    ResponseEntity<Tariff> tariff = cleanAirZonesController
        .tariff(SOME_CHARGE_DEFINITION_ID, "correlation-cleanAirZoneId");

    // then
    assertThat(tariff).isNotNull();
    assertThat(tariff.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(tariff.getBody()).isEqualTo(prepareTariff().get());
  }

  @Test
  public void shouldReturnNotFoundForTariff() {
    // given

    // when
    ResponseEntity<Tariff> tariff = cleanAirZonesController
        .tariff(SOME_CHARGE_DEFINITION_ID, "correlation-cleanAirZoneId");

    // then
    assertThat(tariff.getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_FOUND);
  }

  public Optional<Tariff> prepareTariff() {
    InformationUrls informationUrls = InformationUrls.builder()
        .becomeCompliant(SOME_URL)
        .boundary(SOME_URL)
        .exemptionOrDiscount(SOME_URL)
        .hoursOfOperation(SOME_URL)
        .payCaz(SOME_URL)
        .pricing(SOME_URL)
        .mainInfo(SOME_URL)
        .financialAssistance(SOME_URL)
        .build();
    Rates rates = Rates.builder()
        .bus(new BigDecimal("5.50"))
        .coach(new BigDecimal("15.60"))
        .hgv(new BigDecimal("5.69"))
        .largeVan(new BigDecimal("100.00"))
        .moped(new BigDecimal("49.49"))
        .motorcycle(new BigDecimal("80.01"))
        .phv(new BigDecimal("80.10"))
        .smallVan(new BigDecimal("80.00"))
        .taxi(new BigDecimal("2.00"))
        .build();
    Tariff tariff = Tariff.builder()
        .cleanAirZoneId(SOME_CHARGE_DEFINITION_ID)
        .name("Leeds")
        .tariffClass('C')
        .informationUrls(informationUrls)
        .rates(rates)
        .build();

    return Optional.ofNullable(tariff);
  }

  private CleanAirZones prepareCleanAirZones() {
    return new CleanAirZones(
        newArrayList(
            caz("A", 4,
                "https://www.birmingham.gov.uk/info/20076/pollution/"
                    + "1763/a_clean_air_zone_for_birmingham/3"),

            caz("B", 5,
                "https://www.arcgis.com/home/webmap/viewer.html?webmap="
                    + "de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621")
        ));
  }

  private CleanAirZone caz(String cazName, Integer id, String boundaryUrl) {
    return CleanAirZone.builder()
        .name(cazName)
        .cleanAirZoneId(id)
        .boundaryUrl(URI.create(boundaryUrl))
        .build();
  }
}