package uk.gov.caz.tariff.controller;

import static com.google.common.collect.Lists.newArrayList;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;

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
   * Temporary method that returns hardcoded example Clean Air Zones.
   *
   * @return hardcoded temporary example Clean Air Zones.
   */
  private @Valid CleanAirZones prepareMockedCleanAirZones() {
    return new CleanAirZones(
        newArrayList(
            caz("Birmingham", "42395f51-e924-42b4-8585-b1749dc05bfc",
                "https://www.birmingham.gov.uk/info/20076/"
                    + "pollution/1763/a_clean_air_zone_for_birmingham"),

            caz("Leeds", "146bbfd3-1928-41d3-9575-5f9e58e61ee1",
                "https://www.leeds.gov.uk/business/environmental-health-for-business/air-quality")
        ));
  }

  /**
   * Temporary method that returns fully created CAZ.
   *
   * @param cazName name of CAZ
   * @param cazId ID of the zone
   * @param infoUrl URL provided by zone operators, contains details about tariffs
   * @return hardcoded temporary example Clean Air Zone.
   */
  private CleanAirZone caz(String cazName, String cazId, String infoUrl) {
    return CleanAirZone.builder()
        .name(cazName)
        .id(cazId)
        .infoUrl(URI.create(infoUrl))
        .build();
  }
}
