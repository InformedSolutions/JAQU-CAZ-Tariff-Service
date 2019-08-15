package uk.gov.caz.tariff.service;

import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.gov.caz.tariff.dto.CleanAirZone;
import uk.gov.caz.tariff.dto.CleanAirZones;

/**
 * A class that is responsible for managing cleanAirZone data ({@link CleanAirZone} entities) in the
 * postgres database.
 */
@Repository
public class CleanAirZonesRepository {

  @VisibleForTesting
  static final String SELECT_ALL_SQL = "SELECT cha.charge_definition_id, "
      + "cha.caz_name, "
      + "link.boundary_url "
      + "FROM t_charge_definition cha, t_caz_link_detail link "
      + "WHERE link.charge_definition_id = cha.charge_definition_id ";

  private static final CleanAirZoneRowMapper MAPPER = new CleanAirZoneRowMapper();

  private final JdbcTemplate jdbcTemplate;

  public CleanAirZonesRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public CleanAirZones findAll() {
    return new CleanAirZones(jdbcTemplate.query(SELECT_ALL_SQL, MAPPER));
  }

  public static class CleanAirZoneRowMapper implements RowMapper<CleanAirZone> {

    @Override
    public CleanAirZone mapRow(ResultSet rs, int i) throws SQLException {
      return CleanAirZone.builder()
          .cleanAirZoneId(rs.getInt("charge_definition_id"))
          .name(rs.getString("caz_name"))
          .boundaryUrl(URI.create(rs.getString("boundary_url")))
          .build();
    }
  }
}