package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import uk.gov.caz.tariff.annotation.IntegrationTest;
import uk.gov.caz.tariff.dto.CleanAirZones;
import uk.gov.caz.tariff.service.CleanAirZonesRepository;
import uk.gov.caz.tariff.util.DatabaseInitializer;

@IntegrationTest
@Import(DatabaseInitializer.class)
public class CleanAirZoneRepositoryTestIT {

  @Autowired
  private DatabaseInitializer databaseInitializer;

  @Autowired
  private CleanAirZonesRepository cleanAirZonesRepository;

  @BeforeEach
  public void init() throws Exception {
    databaseInitializer.clear();
    databaseInitializer.initSampleData();
  }

  @Test
  public void shouldReturnCleanAirZones() {
    // given

    // when
    CleanAirZones cleanAirZones = cleanAirZonesRepository.findAll();

    // then
    assertThat(cleanAirZones.getCleanAirZones()).hasSize(2);
  }
}