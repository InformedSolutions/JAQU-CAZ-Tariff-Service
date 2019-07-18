package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import uk.gov.caz.tariff.annotation.IntegrationTest;

@IntegrationTest
class AuditTrailTestIT {

  private static final String AUDIT_LOGGED_ACTIONS_TABLE = "audit.logged_actions";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void testInsertUpdateDeleteOperationsAgainstAuditTrailTable() {
    clear();
    atTheBeginningAuditLoggedActionsTableShouldBeEmpty();

    // INSERT case
    UUID uuid = UUID.randomUUID();
    whenWeInsertSomeSampleDataIntoCleanAirZonesTable(uuid, "Leeds");

    thenNumberOfRowsInAuditLoggedActionsTableForCleanAirZonesShouldBe(1);
    andThereShouldBeExactlyOneInsertActionLogged();
    withNewData("(" + uuid + ",Leeds,,,,,,,,,,)");

    // UPDATE case
    whenWeUpdateCleanAirZonesTableTo("Birmingham");

    thenNumberOfRowsInAuditLoggedActionsTableForCleanAirZonesShouldBe(2);
    andThereShouldBeExactlyOneUpdateActionLogged();
    withNewData("(" + uuid + ",Birmingham,,,,,,,,,,)");

    // DELETE case
    whenWeDeleteRowFromCleanAirZonesTable();

    thenNumberOfRowsInAuditLoggedActionsTableForCleanAirZonesShouldBe(3);
    andThereShouldBeExactlyOneDeleteActionLogged();
    withNewDataEqualToNull();
  }

  private void atTheBeginningAuditLoggedActionsTableShouldBeEmpty() {
    checkIfAuditTableContainsNumberOfRows(0);
  }

  private void whenWeInsertSomeSampleDataIntoCleanAirZonesTable(UUID uuid,
      String cleanAirZoneName) {
    jdbcTemplate.update(
        "INSERT INTO public.t_clean_air_zones (clean_air_zone_id, name) VALUES (?, ?)",
        uuid, cleanAirZoneName);
  }

  private void thenNumberOfRowsInAuditLoggedActionsTableForCleanAirZonesShouldBe(
      int expectedNumberOfRows) {
    checkIfAuditTableContainsNumberOfRows(expectedNumberOfRows,
        "TABLE_NAME = 't_clean_air_zones'");
  }

  private void andThereShouldBeExactlyOneInsertActionLogged() {
    checkIfAuditTableContainsNumberOfRows(1, "action = 'I'");
  }

  private void withNewData(String expectedNewData) {
    checkIfAuditTableContainsNumberOfRows(1, "new_data = '" + expectedNewData + "'");
  }

  private void whenWeUpdateCleanAirZonesTableTo(String vehicleType) {
    jdbcTemplate.update(
        "UPDATE public.t_clean_air_zones set name = ?",
        vehicleType);
  }

  private void andThereShouldBeExactlyOneUpdateActionLogged() {
    checkIfAuditTableContainsNumberOfRows(1, "action = 'U'");
  }

  private void whenWeDeleteRowFromCleanAirZonesTable() {
    jdbcTemplate.update("DELETE from public.t_clean_air_zones");
  }

  private void andThereShouldBeExactlyOneDeleteActionLogged() {
    checkIfAuditTableContainsNumberOfRows(1, "action = 'D'");
  }

  private void withNewDataEqualToNull() {
    checkIfAuditTableContainsNumberOfRows(1, "new_data is null");
  }

  private void checkIfAuditTableContainsNumberOfRows(int expectedNumberOfRowsInAuditTable) {
    int numberOfRowsInAuditTable =
        JdbcTestUtils.countRowsInTable(jdbcTemplate, AUDIT_LOGGED_ACTIONS_TABLE);
    assertThat(numberOfRowsInAuditTable)
        .as("Expected %s row(s) in " + AUDIT_LOGGED_ACTIONS_TABLE + " table",
            expectedNumberOfRowsInAuditTable)
        .isEqualTo(expectedNumberOfRowsInAuditTable);
  }

  private void checkIfAuditTableContainsNumberOfRows(int expectedNumberOfRowsInAuditTable,
      String whereClause) {
    int numberOfRowsInAuditTable =
        JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, AUDIT_LOGGED_ACTIONS_TABLE, whereClause);
    assertThat(numberOfRowsInAuditTable)
        .as("Expected %s row(s) in " + AUDIT_LOGGED_ACTIONS_TABLE
                + " table matching where clause '%s'",
            expectedNumberOfRowsInAuditTable, whereClause)
        .isEqualTo(expectedNumberOfRowsInAuditTable);
  }

  private void clear() {
    jdbcTemplate.execute("TRUNCATE TABLE T_CLEAN_AIR_ZONES CASCADE");
    jdbcTemplate.execute("TRUNCATE TABLE audit.logged_actions CASCADE");
  }
}