package uk.gov.caz.tariff.service;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;
import uk.gov.caz.tariff.service.CleanAirZonesRepository.CleanAirZoneRowMapper;

@ExtendWith(MockitoExtension.class)
class CleanAirZonesRepositoryTest {

  private static final UUID SOME_CLEAN_AIR_ZONE_ID = UUID.randomUUID();

  private static final String SOME_URL = "www.test.uk";

  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private CleanAirZonesRepository cleanAirZonesRepository;

  @Test
  public void shouldFindCleanAirZones() {
    // given
    List<CleanAirZone> cleanAirZones = mockCleanAirZonesInDB();

    // when
    CleanAirZones result = cleanAirZonesRepository.findAll();

    // then
    assertThat(result.getCleanAirZones()).hasSize(2);
    assertThat(result.getCleanAirZones().get(0)).isEqualTo(cleanAirZones.get(0));
    assertThat(result.getCleanAirZones().get(1)).isEqualTo(cleanAirZones.get(1));
  }

  @Test
  public void shouldReturnEmptyList() {
    // given

    // when
    CleanAirZones result = cleanAirZonesRepository.findAll();

    // then
    assertThat(result.getCleanAirZones()).hasSize(0);
  }

  @Nested
  class RowMapper {

    private CleanAirZoneRowMapper rowMapper = new CleanAirZoneRowMapper();

    @Test
    public void shouldMapResultSetToTariff() throws SQLException {
      String name = "Leeds";
      ResultSet resultSet = mockResultSet(SOME_CLEAN_AIR_ZONE_ID, name);

      CleanAirZone cleanAirZone = rowMapper.mapRow(resultSet, 0);

      assertThat(cleanAirZone).isNotNull();
      assertThat(cleanAirZone.getCleanAirZoneId()).isEqualTo(SOME_CLEAN_AIR_ZONE_ID);
      assertThat(cleanAirZone.getName()).isEqualTo(name);
      assertThat(cleanAirZone.getBoundaryUrl().toString()).isEqualTo(SOME_URL);
    }

    private ResultSet mockResultSet(UUID uuid, String name) throws SQLException {
      ResultSet resultSet = mock(ResultSet.class);
      when(resultSet.getObject("clean_air_zone_id", UUID.class)).thenReturn(uuid);

      when(resultSet.getString(anyString())).thenAnswer(answer -> {
        String argument = answer.getArgument(0);
        switch (argument) {
          case "name":
            return name;
          case "boundary_url":
            return SOME_URL;

        }
        throw new RuntimeException("Value not stubbed!");
      });

      return resultSet;
    }
  }

  private List<CleanAirZone> mockCleanAirZonesInDB() {
    List<CleanAirZone> cleanAirZones = prepareCleanAirZones();
    when(jdbcTemplate.query(anyString(), any(CleanAirZoneRowMapper.class))).thenReturn(cleanAirZones);
    return cleanAirZones;
  }

  private List<CleanAirZone> prepareCleanAirZones() {
    return new CleanAirZones(
        newArrayList(
            caz("Birmingham", UUID.fromString("42395f51-e924-42b4-8585-b1749dc05bfc"),
                "https://www.birmingham.gov.uk/info/20076/pollution/"
                    + "1763/a_clean_air_zone_for_birmingham/3)"),

            caz("Leeds", UUID.fromString("146bbfd3-1928-41d3-9575-5f9e58e61ee1"),
                "https://www.arcgis.com/home/webmap/viewer.html?webmap="
                    + "de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621")
        )).getCleanAirZones();
  }

  private CleanAirZone caz(String cazName, UUID cazId, String boundaryUrl) {
    return CleanAirZone.builder()
        .name(cazName)
        .cleanAirZoneId(cazId)
        .boundaryUrl(URI.create(boundaryUrl))
        .build();
  }
}