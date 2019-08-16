package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
        .andExpect(content().json(cleanAirZonesJson()))
        .andExpect(header().string(CORRELATION_ID_HEADER, SOME_CORRELATION_ID));
  }

  @Test
  public void shouldReturnTariffAndStatusOk() throws Exception {
    mockMvc.perform(get(tariffWithCleanAirZoneId("5cd7441d-766f-48ff-b8ad-1809586fea37"))
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .header(CORRELATION_ID_HEADER, SOME_CORRELATION_ID))
        .andExpect(content().json(tariffJson()))
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

  private String tariffJson() throws IOException {
    return readJsonFile("data/json/tariff.json");
  }

  private String cleanAirZonesJson() throws IOException {
    return readJsonFile("data/json/clean-air-zones.json");
  }

  private String readJsonFile(String path) throws IOException {
    return Resources.toString(Resources.getResource(path), Charsets.UTF_8);
  }
}