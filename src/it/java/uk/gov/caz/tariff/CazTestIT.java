package uk.gov.caz.tariff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.caz.tariff.util.Constants.CORRELATION_ID_HEADER;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.caz.tariff.annotation.MockedMvcIntegrationTest;
import uk.gov.caz.tariff.controller.CleanAirZonesController;
import uk.gov.caz.tariff.util.DatabaseInitializer;

@MockedMvcIntegrationTest
@Import(DatabaseInitializer.class)
public class CazTestIT {

  private static final String SOME_CORRELATION_ID = "63be7528-7efd-4f31-ae68-11a6b709ff1c";

  @Autowired
  private DatabaseInitializer databaseInitializer;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void init() throws Exception {
    databaseInitializer.clear();
    databaseInitializer.initSampleData();
  }

  @Test
  public void shouldReturnCleanAirZones() throws Exception {
    mockMvc.perform(get(CleanAirZonesController.PATH)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cleanAirZones[0].cleanAirZoneId")
            .value("1"))
        .andExpect(jsonPath("$.cleanAirZones[0].name").value("Z"))
        .andExpect(jsonPath("$.cleanAirZones[0].boundaryUrl")
            .value("https://www.birmingham.gov.uk/info/20076/pollution/"
                + "1763/a_clean_air_zone_for_birmingham/3"))
        .andExpect(jsonPath("$.cleanAirZones[1].cleanAirZoneId")
            .value("2"))
        .andExpect(jsonPath("$.cleanAirZones[1].name").value("X"))
        .andExpect(jsonPath("$.cleanAirZones[1].boundaryUrl")
            .value("https://www.arcgis.com/home/webmap/viewer.html?webmap="
                + "de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621"))
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnTariffAndStatusOk() throws Exception {
    mockMvc.perform(get(tariffPathWithChargeDefinitionId(1))
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(content().json(readTariffJson()))
        .andExpect(status().isOk())
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnNotFoundWhenTariffNotExist() throws Exception {
    mockMvc.perform(get(tariffPathWithChargeDefinitionId(3))
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isNotFound())
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  private static String tariffPathWithChargeDefinitionId(Integer chargeDefinitionId) {
    return CleanAirZonesController.PATH + "/" + chargeDefinitionId + "/tariff";
  }

  private String readTariffJson() throws IOException {
    return Resources.toString(Resources.getResource("data/json/real-tariff.json"), Charsets.UTF_8);
  }
}