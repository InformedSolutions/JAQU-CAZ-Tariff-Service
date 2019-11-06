package uk.gov.caz.tariff.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.caz.tariff.dto.LambdaContainerStats;

@RestController
public class WarmupController implements WarmupControllerApiSpec {
  public static final String PATH = "/v1/clean-air-zones/warmup";
  
  @Override
  public String warmup() throws JsonProcessingException {
    return LambdaContainerStats.getStats();
  }

}
