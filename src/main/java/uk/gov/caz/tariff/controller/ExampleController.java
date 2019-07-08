package uk.gov.caz.tariff.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.caz.tariff.service.CsvImporterService;
import uk.gov.caz.tariff.service.CsvImporterService.NewCsvEvent;

@RestController
@RequestMapping(
    value = "/entities",
    produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE}
)
@Api(value = "/entities")
@Slf4j
public class ExampleController {

  static final String NAME = "John Doe";

  @Autowired
  CsvImporterService service;

  @GetMapping("/{id}")
  @ApiOperation(value = "Find an entity by id", response = Entity.class)
  public Entity getById(@PathVariable("id") String id) {
    log.info("Handling `getById'");
    return new Entity(NAME, id);
  }

  @PostMapping("/newCsvEvent")
  @Profile("dev")
  public String newCsvFileEventHandler(NewCsvEvent snsEvent) {
    return service.importCsv(snsEvent);
  }

  @Value
  static class Entity {

    String name;
    String id;
  }
}
