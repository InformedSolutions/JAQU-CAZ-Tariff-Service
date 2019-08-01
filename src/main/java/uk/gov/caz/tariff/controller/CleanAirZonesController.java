package uk.gov.caz.tariff.controller;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.caz.tariff.dto.CleanAirZones;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.CleanAirZonesRepository;
import uk.gov.caz.tariff.service.TariffRepository;

/**
 * REST Controller exposing endpoints related to Clean Air Zones.
 */
@RestController
@Slf4j
public class CleanAirZonesController implements CleanAirZonesControllerApiSpec {

  static final String PATH = "/v1/clean-air-zones";

  private final TariffRepository tariffRepository;

  private final CleanAirZonesRepository cleanAirZonesRepository;

  public CleanAirZonesController(TariffRepository tariffRepository,
      CleanAirZonesRepository cleanAirZonesRepository) {
    this.tariffRepository = tariffRepository;
    this.cleanAirZonesRepository = cleanAirZonesRepository;
  }

  @Override
  public ResponseEntity<CleanAirZones> cleanAirZones(
      @RequestHeader("X-Correlation-ID") String correlationId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .header("X-Correlation-ID", correlationId)
        .body(cleanAirZonesRepository.findAll());
  }

  @Override
  public ResponseEntity<Tariff> tariff(@PathVariable UUID cleanAirZoneId,
      @RequestHeader("X-Correlation-ID") String correlationId) {
    return tariffRepository.findByCleanAirZoneId(cleanAirZoneId)
        .map(tariff -> ResponseEntity
            .status(HttpStatus.OK)
            .header("X-Correlation-ID", correlationId)
            .body(tariff))
        .orElseGet(() -> ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .header("X-Correlation-ID", correlationId)
            .build());
  }
}
