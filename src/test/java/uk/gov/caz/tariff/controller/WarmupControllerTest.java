package uk.gov.caz.tariff.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.caz.correlationid.Configuration;
import uk.gov.caz.tariff.controller.WarmupController;

@ContextConfiguration(classes = {Configuration.class, WarmupController.class})
@WebMvcTest
public class WarmupControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void warmupLambdaContainerShouldBeSuccessful() throws Exception {
    sendARequestToWarmupLambdaContainer();
  }
  
  private void sendARequestToWarmupLambdaContainer()
      throws Exception {
     mockMvc.perform(
        get(WarmupController.PATH)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("X-Correlation-ID", "CorrelationId")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Correlation-ID", "CorrelationId"))
        .andExpect(jsonPath("$.instanceId").exists());
  }
}
