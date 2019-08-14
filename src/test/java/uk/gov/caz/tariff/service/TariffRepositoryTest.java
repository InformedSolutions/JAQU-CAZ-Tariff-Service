package uk.gov.caz.tariff.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.caz.tariff.dto.Tariff;
import uk.gov.caz.tariff.service.TariffRepository.TariffRowMapper;

@ExtendWith(MockitoExtension.class)
class TariffRepositoryTest {

  private static final UUID SOME_CLEAN_AIR_ZONE_ID = UUID.randomUUID();

  private static final String SOME_URL = "www.test.uk";

  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private TariffRepository tariffRepository;

  @Test
  public void shouldFindSomeTariff() {
    // given
    Tariff tariff = mockTariffInDB();

    // when
    Optional<Tariff> result = tariffRepository.findByCleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID);

    // then
    assertThat(result).isPresent();
    assertThat(result).contains(tariff);
  }

  @Test
  public void shouldNotFindAnyTariff() {
    // given

    // when
    Optional<Tariff> result = tariffRepository.findByCleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  public void shouldReturnEmptyOptionalWhenThrowException() {
    // given
    when(jdbcTemplate.queryForObject(anyString(), any(TariffRowMapper.class), any()))
        .thenThrow(EmptyResultDataAccessException.class);

    // when
    Optional<Tariff> result = tariffRepository.findByCleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID);

    // then
    assertThat(result).isEmpty();
  }

  @Nested
  class RowMapper {

    private TariffRowMapper rowMapper = new TariffRowMapper();

    @Test
    public void shouldMapResultSetToTariff() throws SQLException {
      String name = "Leeds";
      ResultSet resultSet = mockResultSet(SOME_CLEAN_AIR_ZONE_ID, name);

      Tariff tariff = rowMapper.mapRow(resultSet, 0);

      assertThat(tariff).isNotNull();
      assertThat(tariff.getCleanAirZoneId()).isEqualTo(SOME_CLEAN_AIR_ZONE_ID);
      assertThat(tariff.getName()).isEqualTo(name);
      assertThat(tariff.getTariffClass()).isEqualTo('C');
      assertThat(tariff.isMotorcyclesChargeable()).isEqualTo(false);
      assertThat(tariff.getInformationUrls().getBecomeCompliant()).isEqualTo(SOME_URL);
      assertThat(tariff.getRates().getBus()).isEqualTo("50.55");
      assertThat(tariff.getRates().getCoach()).isEqualTo("50.00");
      assertThat(tariff.getRates().getTaxi()).isEqualTo("15.10");
      assertThat(tariff.getRates().getPhv()).isEqualTo("15.35");
      assertThat(tariff.getRates().getHgv()).isEqualTo("5.30");
      assertThat(tariff.getRates().getLargeVan()).isEqualTo("80.30");
      assertThat(tariff.getRates().getMiniBus()).isEqualTo("100.30");
      assertThat(tariff.getRates().getSmallVan()).isEqualTo("100.00");
      assertThat(tariff.getRates().getCar()).isEqualTo("25.01");
      assertThat(tariff.getRates().getMotorcycle()).isEqualTo("25.10");
      assertThat(tariff.getRates().getMoped()).isEqualTo("49.49");
    }

    private ResultSet mockResultSet(UUID uuid, String name) throws SQLException {
      ResultSet resultSet = mock(ResultSet.class);
      when(resultSet.getObject("clean_air_zone_id", UUID.class)).thenReturn(uuid);
      when(resultSet.getBoolean("motorcycles_chargeable")).thenReturn(false);

      when(resultSet.getString(anyString())).thenAnswer(answer -> {
        String argument = answer.getArgument(0);
        switch (argument) {
          case "name":
            return name;
          case "tariff_class":
            return String.valueOf('C');
          case "become_compliant_url":
          case "hours_of_operation_url":
          case "emissions_standards_url":
          case "main_info_url":
          case "pricing_url":
          case "exemption_or_discount_url":
          case "pay_caz_url":
          case "financial_assistance_url":
          case "boundary_url":
            return SOME_URL;

        }
        throw new RuntimeException("Value not stubbed!");
      });

      when(resultSet.getBigDecimal(any())).thenAnswer(answer -> {
        String argument = answer.getArgument(0);
        switch (argument) {
          case "bus":
            return new BigDecimal("50.55");
          case "coach":
            return new BigDecimal("50.00");
          case "taxi":
            return new BigDecimal("15.10");
          case "phv":
            return new BigDecimal("15.35");
          case "hgv":
            return new BigDecimal("5.30");
          case "large_van":
            return new BigDecimal("80.30");
          case "minibus":
            return new BigDecimal("100.30");
          case "small_van":
            return new BigDecimal("100.00");
          case "car":
            return new BigDecimal("25.01");
          case "motorcycle":
            return new BigDecimal("25.10");
          case "moped":
            return new BigDecimal("49.49");

        }
        throw new RuntimeException("Value not stubbed!");
      });

      return resultSet;
    }
  }

  private Tariff mockTariffInDB() {
    Tariff tariff = Tariff.builder()
        .cleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID)
        .name("Leeds")
        .build();
    when(jdbcTemplate.queryForObject(anyString(), any(TariffRowMapper.class), any())).thenReturn(
        tariff);
    return tariff;
  }
}