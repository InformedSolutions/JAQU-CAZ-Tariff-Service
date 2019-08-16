package uk.gov.caz.tariff.controller;

import static uk.gov.caz.tariff.util.Constants.CORRELATION_ID_HEADER;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.gov.caz.tariff.dto.CleanAirZones;
import uk.gov.caz.tariff.dto.Tariff;

@RequestMapping(
    value = CleanAirZonesController.PATH,
    produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
)
@Api(value = CleanAirZonesController.PATH)
public interface CleanAirZonesControllerApiSpec {

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
      @ApiImplicitParam(name = CORRELATION_ID_HEADER,
          required = true,
          value = "UUID formatted string to track the request through the enquiries stack",
          paramType = "header")
  })
  @GetMapping
  @ResponseStatus(value = HttpStatus.OK)
  ResponseEntity<CleanAirZones> cleanAirZones(
      @RequestHeader(CORRELATION_ID_HEADER) String correlationId);

  /**
   * Returns details of Tariff.
   *
   * @param correlationId UUID formatted string to track the request through the enquiries
   *     stack.
   * @param cleanAirZoneId UUID of zone for which tariffs will be fetched.
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
      @ApiImplicitParam(name = CORRELATION_ID_HEADER,
          required = true,
          value = "UUID formatted string to track the request through the enquiries stack",
          paramType = "header")
  })
  @GetMapping("/{cleanAirZoneId}/tariff")
  @ResponseStatus(value = HttpStatus.OK)
  ResponseEntity<Tariff> tariff(@PathVariable String cleanAirZoneId,
      @RequestHeader(CORRELATION_ID_HEADER) String correlationId);
}