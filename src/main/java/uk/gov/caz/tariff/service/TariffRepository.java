package uk.gov.caz.tariff.service;

import com.google.common.annotations.VisibleForTesting;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
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
  static final String SELECT_BY_CLEAN_AIR_ZONE_ID_SQL = "SELECT caz.clean_air_zone_id, "
      + "caz.name, "
      + "caz.tariff_class, "
      + "caz.motorcycles_chargeable, "
      + "caz.emissions_standards_url, "
      + "caz.main_info_url, "
      + "caz.pricing_url, "
      + "caz.hours_of_operation_url, "
      + "caz.exemption_or_discount_url, "
      + "caz.pay_caz_url, "
      + "caz.become_compliant_url, "
      + "caz.financial_assistance_url, "
      + "caz.boundary_url, "
      + "ta.bus, "
      + "ta.coach, "
      + "ta.taxi, "
      + "ta.phv, "
      + "ta.hgv, "
      + "ta.large_van, "
      + "ta.minibus, "
      + "ta.small_van, "
      + "ta.car, "
      + "ta.motorcycle, "
      + "ta.moped "
      + "FROM t_clean_air_zones caz, t_tariffs ta "
      + "WHERE caz.clean_air_zone_id = ta.clean_air_zone_id "
      + "AND caz.clean_air_zone_id = ?";

  private static final TariffRowMapper MAPPER = new TariffRowMapper();

  private final JdbcTemplate jdbcTemplate;

  public TariffRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Finds {@link Tariff} by cleanAirZoneId.
   *
   * @param cleanAirZoneId UUID of tariff that will be fetched.
   * @return An {@link Optional} of {@link Tariff}
   */
  public Optional<Tariff> findByCleanAirZoneId(UUID cleanAirZoneId) {
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(SELECT_BY_CLEAN_AIR_ZONE_ID_SQL, MAPPER, cleanAirZoneId));
    } catch (EmptyResultDataAccessException exc) {
      return Optional.empty();
    }
  }

  public static class TariffRowMapper implements RowMapper<Tariff> {

    @Override
    public Tariff mapRow(ResultSet rs, int i) throws SQLException {
      return Tariff.builder()
          .cleanAirZoneId(rs.getObject("clean_air_zone_id", UUID.class))
          .name(rs.getString("name"))
          .tariffClass(rs.getString("tariff_class").charAt(0))
          .motorcyclesChargeable(rs.getBoolean("motorcycles_chargeable"))
          .informationUrls(InformationUrls.builder()
              .becomeCompliant(rs.getString("become_compliant_url"))
              .hoursOfOperation(rs.getString("hours_of_operation_url"))
              .emissionsStandards(rs.getString("emissions_standards_url"))
              .mainInfo(rs.getString("main_info_url"))
              .pricing(rs.getString("pricing_url"))
              .exemptionOrDiscount(rs.getString("exemption_or_discount_url"))
              .payCaz(rs.getString("pay_caz_url"))
              .financialAssistance(rs.getString("financial_assistance_url"))
              .boundary(rs.getString("boundary_url"))
              .build())
          .rates(Rates.builder()
              .bus(rs.getBigDecimal("bus"))
              .coach(rs.getBigDecimal("coach"))
              .taxi(rs.getBigDecimal("taxi"))
              .phv(rs.getBigDecimal("phv"))
              .hgv(rs.getBigDecimal("hgv"))
              .largeVan(rs.getBigDecimal("large_van"))
              .miniBus(rs.getBigDecimal("minibus"))
              .smallVan(rs.getBigDecimal("small_van"))
              .car(rs.getBigDecimal("car"))
              .motorcycle(rs.getBigDecimal("motorcycle"))
              .moped(rs.getBigDecimal("moped"))
              .build())
          .build();
    }
  }
}