package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;

class CleanAirZonesControllerTest {

  private CleanAirZonesController cleanAirZonesController = new CleanAirZonesController();

  @Test
  public void shouldReturnSomeSampleHardcodedData() {
    // given

    // when
    ResponseEntity<CleanAirZones> cleanAirZones = cleanAirZonesController
        .cleanAirZones("correlation-id");

    // then
    assertThat(cleanAirZones).isNotNull();
    assertThat(cleanAirZones.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(cleanAirZones.getBody().getCleanAirZones()).contains(
        CleanAirZone.builder()
            .name("Birmingham")
            .id("42395f51-e924-42b4-8585-b1749dc05bfc")
            .infoUrl(URI.create("https://www.birmingham.gov.uk/info/20076/"
                + "pollution/1763/a_clean_air_zone_for_birmingham"))
            .build()
    );
  }

  @Test
  public void shouldReturnIncomingXCorrelationId() {
    // given

    // when
    ResponseEntity<CleanAirZones> cleanAirZones = cleanAirZonesController
        .cleanAirZones("correlation-id");

    // then
    assertThat(cleanAirZones).isNotNull();
    assertThat(cleanAirZones.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    assertThat(cleanAirZones.getHeaders())
        .containsEntry("X-Correlation-ID", newArrayList("correlation-id"));
  }
}