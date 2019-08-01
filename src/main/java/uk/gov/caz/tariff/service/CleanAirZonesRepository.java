package uk.gov.caz.tariff.service;

import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;

/**
 * A class that is responsible for managing cleanAirZone data ({@link
 * CleanAirZone} entities) in the postgres database.
 */
@Repository
public class CleanAirZonesRepository {

  @VisibleForTesting
  static final String SELECT_ALL_SQL = "SELECT caz.clean_air_zone_id, "
      + "caz.name, "
      + "caz.boundary_url "
      + "FROM t_clean_air_zones caz ";

  private static final CleanAirZoneRowMapper MAPPER = new CleanAirZoneRowMapper();

  private final JdbcTemplate jdbcTemplate;

  public CleanAirZonesRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public CleanAirZones findAll() {
    return new CleanAirZones(jdbcTemplate.query(SELECT_ALL_SQL, MAPPER));
  }

  @VisibleForTesting
  public static class CleanAirZoneRowMapper implements RowMapper<CleanAirZone> {

    @Override
    public CleanAirZone mapRow(ResultSet rs, int i) throws SQLException {
      return CleanAirZone.builder()
          .cleanAirZoneId(rs.getObject("clean_air_zone_id", UUID.class))
          .name(rs.getString("name"))
          .boundaryUrl(URI.create(rs.getString("boundary_url")))
          .build();
    }
  }
}