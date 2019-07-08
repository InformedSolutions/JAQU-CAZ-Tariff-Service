package uk.gov.caz.tariff.service;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CsvImporterService {
  public String importCsv(NewCsvEvent newCsvEvent) {
    log.info("Spring-Boot in Lambda called by SNS event");
    log.info(newCsvEvent.toString());
    return "OK";
  }

  @Value
  static public class NewCsvEvent {
    String pathToCsv;
  }
}
