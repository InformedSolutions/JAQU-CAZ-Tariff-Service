package uk.gov.caz.tariff.service;

import com.google.common.annotations.VisibleForTesting;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import uk.gov.caz.tariff.dto.InformationUrls;
import uk.gov.caz.tariff.dto.Rates;
import uk.gov.caz.tariff.dto.Tariff;

/**
 * A class that is responsible for managing tariff data ({@link Tariff} entities) in the postgres
 * database.
 */
@Repository
public class TariffRepository {

  @VisibleForTesting
  static final String SELECT_BY_CHARGE_DEFINITION_ID_SQL = "SELECT cha.charge_definition_id, "
      + "cha.caz_name, "
      + "cha.caz_category_code, "
      + "link.main_info_url, "
      + "link.pricing_url, "
      + "link.operation_hours_url, "
      + "link.exemption_url, "
      + "link.pay_caz_url, "
      + "link.become_compliant_url, "
      + "link.financial_assistance_url, "
      + "link.boundary_url, "
      + "tar.hgv_entrant_fee, "
      + "tar.taxi_entrant_fee, "
      + "tar.phv_entrant_fee, "
      + "tar.bus_entrant_fee, "
      + "tar.motorcycle_ent_fee, "
      + "tar.coach_entrant_fee, "
      + "tar.large_van_entrant_fee, "
      + "tar.small_van_entrant_fee, "
      + "tar.moped_entrant_fee "
      + "FROM t_tariff_definition tar, t_charge_definition cha, t_caz_link_detail link "
      + "WHERE tar.charge_definition_id = cha.charge_definition_id "
      + "AND link.charge_definition_id = cha.charge_definition_id "
      + "AND cha.charge_definition_id = ?";

  private static final TariffRowMapper MAPPER = new TariffRowMapper();

  private final JdbcTemplate jdbcTemplate;

  public TariffRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Finds {@link Tariff} by cleanAirZoneId.
   *
   * @param cleanAirZoneId ID of zone for which tariffs will be fetched.
   * @return An {@link Optional} of {@link Tariff}
   */
  public Optional<Tariff> findByCleanAirZoneId(Integer cleanAirZoneId) {
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(SELECT_BY_CHARGE_DEFINITION_ID_SQL, MAPPER, cleanAirZoneId));
    } catch (EmptyResultDataAccessException exc) {
      return Optional.empty();
    }
  }

  public static class TariffRowMapper implements RowMapper<Tariff> {

    @Override
    public Tariff mapRow(ResultSet rs, int i) throws SQLException {
      return Tariff.builder()
          .cleanAirZoneId(rs.getInt("charge_definition_id"))
          .name(rs.getString("caz_name"))
          .tariffClass(rs.getString("caz_category_code").charAt(0))
          .informationUrls(InformationUrls.builder()
              .becomeCompliant(rs.getString("become_compliant_url"))
              .hoursOfOperation(rs.getString("operation_hours_url"))
              .mainInfo(rs.getString("main_info_url"))
              .pricing(rs.getString("pricing_url"))
              .exemptionOrDiscount(rs.getString("exemption_url"))
              .payCaz(rs.getString("pay_caz_url"))
              .financialAssistance(rs.getString("financial_assistance_url"))
              .boundary(rs.getString("boundary_url"))
              .build())
          .rates(Rates.builder()
              .bus(rs.getBigDecimal("bus_entrant_fee"))
              .coach(rs.getBigDecimal("coach_entrant_fee"))
              .taxi(rs.getBigDecimal("taxi_entrant_fee"))
              .phv(rs.getBigDecimal("phv_entrant_fee"))
              .hgv(rs.getBigDecimal("hgv_entrant_fee"))
              .largeVan(rs.getBigDecimal("large_van_entrant_fee"))
              .smallVan(rs.getBigDecimal("small_van_entrant_fee"))
              .motorcycle(rs.getBigDecimal("motorcycle_ent_fee"))
              .moped(rs.getBigDecimal("moped_entrant_fee"))
              .build())
          .build();
    }
  }
}