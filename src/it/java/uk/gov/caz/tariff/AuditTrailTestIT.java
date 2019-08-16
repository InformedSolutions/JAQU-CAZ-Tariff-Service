package uk.gov.caz.tariff;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import uk.gov.caz.tariff.annotation.IntegrationTest;
import uk.gov.caz.tariff.util.DatabaseInitializer;

@IntegrationTest
@Import(DatabaseInitializer.class)
class AuditTrailTestIT {

  private static final String AUDIT_LOGGED_ACTIONS_TABLE = "audit.logged_actions";

  @Autowired
  private DatabaseInitializer databaseInitializer;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void testInsertUpdateDeleteOperationsAgainstAuditTrailTable() {
    databaseInitializer.clear();
    atTheBeginningAuditLoggedActionsTableShouldBeEmpty();
    LocalDate date = LocalDate.of(2019, 8, 14);

    // INSERT case
    whenWeInsertSomeSampleDataIntoCazClassTable('A', "Class A", date);

    thenNumberOfRowsInAuditLoggedActionsTableForCazClassShouldBe(1);
    andThereShouldBeExactlyOneInsertActionLogged();
    withNewData("(A,\"Class A\",\"2019-08-14 00:00:00\")");

    // UPDATE case
    whenWeUpdateCazClassTableTo("Classs A");

    thenNumberOfRowsInAuditLoggedActionsTableForCazClassShouldBe(2);
    andThereShouldBeExactlyOneUpdateActionLogged();
    withNewData("(A,\"Classs A\",\"2019-08-14 00:00:00\")");

    // DELETE case
    whenWeDeleteRowFromCazClassTable();

    thenNumberOfRowsInAuditLoggedActionsTableForCazClassShouldBe(3);
    andThereShouldBeExactlyOneDeleteActionLogged();
    withNewDataEqualToNull();
  }

  private void atTheBeginningAuditLoggedActionsTableShouldBeEmpty() {
    checkIfAuditTableContainsNumberOfRows(0);
  }

  private void whenWeInsertSomeSampleDataIntoCazClassTable(char cazClass,
      String cazClassDesc, LocalDate date) {
    jdbcTemplate.update(
        "INSERT INTO public.t_caz_class (caz_class, caz_class_desc, insert_timestmp) VALUES (?, ?, ?)",
        cazClass, cazClassDesc, date);
  }

  private void thenNumberOfRowsInAuditLoggedActionsTableForCazClassShouldBe(
      int expectedNumberOfRows) {
    checkIfAuditTableContainsNumberOfRows(expectedNumberOfRows,
        "TABLE_NAME = 't_caz_class'");
  }

  private void andThereShouldBeExactlyOneInsertActionLogged() {
    checkIfAuditTableContainsNumberOfRows(1, "action = 'I'");
  }

  private void withNewData(String expectedNewData) {
    checkIfAuditTableContainsNumberOfRows(1, "new_data = '" + expectedNewData + "'");
  }

  private void whenWeUpdateCazClassTableTo(String cazClassDesc) {
    jdbcTemplate.update(
        "UPDATE public.t_caz_class set caz_class_desc = ?",
        cazClassDesc);
  }

  private void andThereShouldBeExactlyOneUpdateActionLogged() {
    checkIfAuditTableContainsNumberOfRows(1, "action = 'U'");
  }

  private void whenWeDeleteRowFromCazClassTable() {
    jdbcTemplate.update("DELETE from public.t_caz_class");
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
}