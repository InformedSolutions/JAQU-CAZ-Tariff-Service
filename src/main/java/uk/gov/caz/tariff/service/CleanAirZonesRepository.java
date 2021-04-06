package uk.gov.caz.tariff.service;

import static uk.gov.caz.tariff.service.RepositoryUtils.safelyGetDate;

import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.gov.caz.definitions.dto.CleanAirZoneDto;
import uk.gov.caz.definitions.dto.CleanAirZonesDto;

/**
 * A class that is responsible for managing cleanAirZone data ({@link CleanAirZoneDto} entities) in
 * the postgres database.
 */
@Repository
public class CleanAirZonesRepository {

  @VisibleForTesting
  static final String SELECT_ALL_SQL = "SELECT charge.clean_air_zone_id, "
      + "charge.caz_name, "
      + "charge.active_charge_start_time, "
      + "charge.active_charge_start_date_text, "
      + "charge.display_from, "
      + "charge.display_order, "
      + "charge.caz_operator_name, "
      + "charge.direct_debit_enabled, "
      + "charge.direct_debit_start_date_text, "
      + "link.boundary_url, "
      + "link.exemption_url, "
      + "link.main_info_url, "
      + "link.payments_compliance_url, "
      + "link.privacy_policy_url, "
      + "link.fleets_compliance_url "
      + "FROM t_charge_definition charge, t_caz_link_detail link "
      + "WHERE link.charge_definition_id = charge.charge_definition_id "
      + "ORDER BY charge.display_order ";

  private static final CleanAirZoneRowMapper MAPPER = new CleanAirZoneRowMapper();

  private final JdbcTemplate jdbcTemplate;

  public CleanAirZonesRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Finds every {@link CleanAirZoneDto}.
   *
   * @return {@link CleanAirZonesDto} list of {@link CleanAirZoneDto}
   */
  public CleanAirZonesDto findAll() {
    return CleanAirZonesDto.builder().cleanAirZones(jdbcTemplate.query(SELECT_ALL_SQL, MAPPER))
        .build();
  }

  /**
   * RowMapper for {@link CleanAirZoneDto}.
   */
  public static class CleanAirZoneRowMapper implements RowMapper<CleanAirZoneDto> {

    @Override
    public CleanAirZoneDto mapRow(ResultSet rs, int i) throws SQLException {
      return CleanAirZoneDto.builder()
          .cleanAirZoneId(rs.getObject("clean_air_zone_id", UUID.class))
          .name(rs.getString("caz_name"))
          .boundaryUrl(URI.create(rs.getString("boundary_url")))
          .exemptionUrl(URI.create(rs.getString("exemption_url")))
          .mainInfoUrl(URI.create(rs.getString("main_info_url")))
          .paymentsComplianceUrl(URI.create(rs.getString("payments_compliance_url")))
          .privacyPolicyUrl(URI.create(rs.getString("privacy_policy_url")))
          .fleetsComplianceUrl(URI.create(rs.getString("fleets_compliance_url")))
          .activeChargeStartDate(safelyGetDate(rs, "active_charge_start_time"))
          .activeChargeStartDateText(rs.getString("active_charge_start_date_text"))
          .displayFrom(safelyGetDate(rs, "display_from"))
          .displayOrder(rs.getObject("display_order", Integer.class))
          .operatorName(rs.getString("caz_operator_name"))
          .directDebitEnabled(rs.getBoolean("direct_debit_enabled"))
          .directDebitStartDateText(rs.getString("direct_debit_start_date_text"))
          .build();
    }
  }
}