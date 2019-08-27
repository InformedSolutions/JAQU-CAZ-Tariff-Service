package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import uk.gov.caz.tariff.annotation.IntegrationTest;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.TariffRepository;

@IntegrationTest
@Sql(scripts = {
    "classpath:data/sql/clear.sql",
    "classpath:data/sql/sample-data.sql"},
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class TariffRepositoryTestIT {

  @Autowired
  private TariffRepository tariffRepository;

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
    assertThat(tariff.getRates().getHgv()).isEqualTo(rate(50.00));
    assertThat(tariff.getRates().getCar()).isEqualTo(rate(8.00));
    assertThat(tariff.getRates().getMiniBus()).isEqualTo(rate(50.00));
    assertThat(tariff.getRates().getTaxi()).isEqualTo(rate(8.00));
  }

  private BigDecimal rate(double rate) {
    return new BigDecimal(rate).setScale(2);
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