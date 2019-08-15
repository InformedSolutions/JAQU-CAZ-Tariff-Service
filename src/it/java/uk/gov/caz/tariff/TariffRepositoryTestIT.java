package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import uk.gov.caz.tariff.annotation.IntegrationTest;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.TariffRepository;
import uk.gov.caz.tariff.util.DatabaseInitializer;

@IntegrationTest
@Import(DatabaseInitializer.class)
public class TariffRepositoryTestIT {

  @Autowired
  private DatabaseInitializer databaseInitializer;

  @Autowired
  private TariffRepository tariffRepository;

  @BeforeEach
  public void init() {
    databaseInitializer.clear();
  }

  @AfterEach
  public void clear() {
    databaseInitializer.clear();
  }

  @Test
  public void shouldReturnSampleTariff() throws Exception {
    databaseInitializer.initSampleData();

    Tariff tariff = tariffRepository.findByCleanAirZoneId(1).get();

    System.out.println(tariff.toString());
    assertThat(tariff.getCleanAirZoneId()).isEqualTo(1);
    assertThat(tariff.getName()).isEqualTo("Z");
    assertThat(tariff.getTariffClass()).isEqualTo('A');
  }
}