package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
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
  public void init() throws Exception {
    databaseInitializer.clear();
    databaseInitializer.initSampleData();
  }

  @Test
  public void shouldReturnSampleTariff() {
    // given
    UUID cleanAirZoneId = UUID.fromString("5cd7441d-766f-48ff-b8ad-1809586fea37");

    // when
    Tariff tariff = tariffRepository.findByCleanAirZoneId(cleanAirZoneId).get();

    // then
    assertThat(tariff.getCleanAirZoneId()).isEqualTo(cleanAirZoneId);
    assertThat(tariff.getName()).isEqualTo("Birmingham");
    assertThat(tariff.getTariffClass()).isEqualTo('D');
  }

  @Test
  public void shouldReturnEmptyWhenTariffNotExist() {
    // given
    UUID cleanAirZoneId = UUID.fromString("39cf92a1-de38-4c93-b676-3aba2b3b2e1d");

    // when
    Optional<Tariff> tariff = tariffRepository.findByCleanAirZoneId(cleanAirZoneId);

    // then
    assertThat(tariff).isEmpty();
  }
}