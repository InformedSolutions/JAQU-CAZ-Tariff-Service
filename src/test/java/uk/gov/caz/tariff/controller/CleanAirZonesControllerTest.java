package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.caz.tariff.dto.clean.air.zone.CleanAirZone;
import uk.gov.caz.tariff.dto.clean.air.zone.CleanAirZones;
import uk.gov.caz.tariff.dto.tariff.InformationUrls;
import uk.gov.caz.tariff.dto.tariff.Rates;
import uk.gov.caz.tariff.dto.tariff.Tariff;
import uk.gov.caz.tariff.service.TariffRepository;

@ExtendWith(MockitoExtension.class)
class CleanAirZonesControllerTest {

  private static final UUID SOME_CLEAN_AIR_ZONE_ID = UUID.fromString("8ed3580b-f155-4f6d-ab12-5a96b071a0a7");

  private static final String SOME_URL = "www.test.uk";

  @Mock
  private TariffRepository tariffRepository;

  @InjectMocks
  private CleanAirZonesController cleanAirZonesController;

  @Test
  public void shouldReturnSomeSampleHardcodedData() {
    // given

    // when
    ResponseEntity<CleanAirZones> cleanAirZones = cleanAirZonesController
        .cleanAirZones("correlation-cleanAirZoneId");

    // then
    assertThat(cleanAirZones).isNotNull();
    assertThat(cleanAirZones.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(cleanAirZones.getBody().getCleanAirZones()).contains(
        CleanAirZone.builder()
            .cleanAirZoneId(UUID.fromString("42395f51-e924-42b4-8585-b1749dc05bfc"))
            .name("Birmingham")
            .boundaryUrl(URI.create("https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham/3)"))
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
        .containsEntry("X-Correlation-ID", newArrayList("correlation-cleanAirZoneId"));
  }

  @Test
  public void shouldReturnSomeTariff() {
    // given
    when(tariffRepository.findByCleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID)).thenReturn(prepareTariff());

    // when
    ResponseEntity<Tariff> tariff = cleanAirZonesController
        .tariff(SOME_CLEAN_AIR_ZONE_ID, "correlation-cleanAirZoneId");

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
        .tariff(SOME_CLEAN_AIR_ZONE_ID, "correlation-cleanAirZoneId");

    // then
    assertThat(tariff.getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_FOUND);
  }

  public Optional<Tariff> prepareTariff() {
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
      Tariff tariff = Tariff.builder()
          .cleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID)
          .name("Leeds")
          .motorcyclesChargeable(false)
          .tariffClass('C')
          .informationUrls(informationUrls)
          .rates(rates)
          .build();

    return Optional.ofNullable(tariff);
  }
}