package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.caz.tariff.dto.clean.air.zone.CleanAirZone;
import uk.gov.caz.tariff.dto.clean.air.zone.CleanAirZones;
import uk.gov.caz.tariff.dto.tariff.Tariff;
import uk.gov.caz.tariff.service.TariffRepository;

/**
 * REST Controller exposing endpoints related to Clean Air Zones.
 */
@RestController
@RequestMapping(
    value = CleanAirZonesController.PATH,
    produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
)
@Api(value = CleanAirZonesController.PATH)
@Slf4j
public class CleanAirZonesController {

  static final String PATH = "/v1/clean-air-zones";

  private final TariffRepository tariffRepository;

  public CleanAirZonesController(TariffRepository tariffRepository) {
    this.tariffRepository = tariffRepository;
  }

  /**
   * Returns list of available CAZs (Clean Air Zones).
   *
   * @param correlationId UUID formatted string to track the request through the enquiries
   *     stack.
   * @return List of POJOs with available CAZs (Clean Air Zones).
   */
  @ApiOperation(
      value = "${swagger.operations.cleanAirZones.description}",
      response = CleanAirZones.class
  )
  @ApiResponses({
      @ApiResponse(code = 500, message = "Internal Server Error / No message available"),
      @ApiResponse(code = 405, message = "Method Not Allowed / Request method 'XXX' not supported"),
      @ApiResponse(code = 404, message = "Not Found / No message available"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 400, message = "Bad Request, for example required HTTP Header missing"),
  })
  @ApiImplicitParams({
      @ApiImplicitParam(name = "X-Correlation-ID",
          required = true,
          value = "UUID formatted string to track the request through the enquiries stack",
          paramType = "header")
  })
  @GetMapping
  @ResponseStatus(value = HttpStatus.OK) // for springfox to correctly pick up status code
  public ResponseEntity<CleanAirZones> cleanAirZones(
      @RequestHeader("X-Correlation-ID") String correlationId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .header("X-Correlation-ID", correlationId)
        .body(prepareMockedCleanAirZones());
  }

  /**
   * Returns details of Tariff.
   *
   * @param correlationId UUID formatted string to track the request through the enquiries
   *     stack.
   * @return Tariff details.
   */
  @ApiOperation(
      value = "${swagger.operations.tariff.description}",
      response = Tariff.class
  )
  @ApiResponses({
      @ApiResponse(code = 500, message = "Internal Server Error / No message available"),
      @ApiResponse(code = 405, message = "Method Not Allowed / Request method 'XXX' not supported"),
      @ApiResponse(code = 404, message = "Tariff Not Exist"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 400, message = "Bad Request, for example required HTTP Header missing"),
  })
  @ApiImplicitParams({
      @ApiImplicitParam(name = "X-Correlation-ID",
          required = true,
          value = "UUID formatted string to track the request through the enquiries stack",
          paramType = "header")
  })
  @GetMapping("/{cleanAirZoneId}/tariff")
  @ResponseStatus(value = HttpStatus.OK)
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

  /**
   * Temporary method that returns hardcoded example Clean Air Zones.
   *
   * @return hardcoded temporary example Clean Air Zones.
   */
  private @Valid CleanAirZones prepareMockedCleanAirZones() {
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

  /**
   * Temporary method that returns fully created Tariff.
   *
   * @param cazName name of Tariff
   * @param cazId ID of the zone
   * @param boundaryUrl URL provided by zone operators, contains tariff about tariffs
   * @return hardcoded temporary example Clean Air Zone.
   */
  private CleanAirZone caz(String cazName, UUID cazId, String boundaryUrl) {
    return CleanAirZone.builder()
        .name(cazName)
        .cleanAirZoneId(cazId)
        .boundaryUrl(URI.create(boundaryUrl))
        .build();
  }
}
