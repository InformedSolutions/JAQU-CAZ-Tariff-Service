package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;
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
            .value("0d7ab5c4-5fff-4935-8c4e-56267c0c9493"))
        .andExpect(jsonPath("$.cleanAirZones[0].name").value("Birmingham"))
        .andExpect(jsonPath("$.cleanAirZones[0].boundaryUrl")
            .value("https://www.birmingham.gov.uk/info/20076/pollution/"
                + "1763/a_clean_air_zone_for_birmingham/3"))
        .andExpect(jsonPath("$.cleanAirZones[1].cleanAirZoneId")
            .value("39e54ed8-3ed2-441d-be3f-38fc9b70c8d3"))
        .andExpect(jsonPath("$.cleanAirZones[1].name").value("Leeds"))
        .andExpect(jsonPath("$.cleanAirZones[1].boundaryUrl")
            .value("https://www.arcgis.com/home/webmap/viewer.html?webmap="
                + "de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621"))
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnTariffAndStatusOk() throws Exception {
    mockMvc.perform(get(tariffWithCleanAirZoneId("0d7ab5c4-5fff-4935-8c4e-56267c0c9493"))
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(content().json(readTariffJson()))
        .andExpect(status().isOk())
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnNotFoundWhenTariffNotExist() throws Exception {
    mockMvc.perform(get(tariffWithCleanAirZoneId("dc1efcaf-a2cf-41ec-aa37-ea4b28a20a1d"))
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isNotFound())
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnNotFoundWhenInvalidUUID() throws Exception {
    // when
    Exception resolvedException = mockMvc.perform(get(tariffWithCleanAirZoneId("asd"))
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(status().isNotFound())
        .andReturn()
        .getResolvedException();

    // then
    assertThat(resolvedException).isNotNull();
    assertThat(resolvedException).isInstanceOf(IllegalArgumentException.class);
  }

  private static String tariffWithCleanAirZoneId(String cleanAirZoneId) {
    return CleanAirZonesController.PATH + "/" + cleanAirZoneId + "/tariff";
  }

  private String readTariffJson() throws IOException {
    return Resources.toString(Resources.getResource("data/json/real-tariff.json"), Charsets.UTF_8);
  }
}