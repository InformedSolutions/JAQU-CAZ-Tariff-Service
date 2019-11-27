package uk.gov.caz.tariff.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LiquibaseWrapper {

  private final DataSource dataSource;
  
  @Value("${spring.liquibase.change-log:db/changelog/db.changelog-master.yaml}")
  private String liquibaseChangeLog;
  
  /**
   * Default constructor.
   * @param dataSource liquibase data source.
   */
  public LiquibaseWrapper(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  /**
   * Update db schema.
   * @throws SQLException when unable to connect to DB
   * @throws LiquibaseException when fail to perform DB upgrade
   */
  public void update() throws LiquibaseException, SQLException {
    String changelog = liquibaseChangeLog.startsWith("classpath")
        ? liquibaseChangeLog.substring(10) : liquibaseChangeLog;
    String dbTag = UUID.randomUUID().toString();
    Contexts contexts = new Contexts();
    try (Connection connection = dataSource.getConnection()) {
      Database database = DatabaseFactory.getInstance()
          .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      Liquibase liquibase = new Liquibase(changelog, 
          new ClassLoaderResourceAccessor(), database);
      //tag DB for future rollback when db update fail
      liquibase.tag(dbTag);
      try {
        liquibase.update(contexts);
      } catch (LiquibaseException lqbEx) {
        log.error(lqbEx.getMessage());
        log.info("Attempting to rollback db update");
        liquibase.rollback(dbTag, contexts);
        throw lqbEx;
      }
    }
  }
}
