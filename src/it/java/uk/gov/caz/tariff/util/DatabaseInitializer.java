package uk.gov.caz.tariff.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

  private static final List<Path> SAMPLE_DATA = Collections.singletonList(
      createTestSqlDataPath("sample-data.sql")
  );

  private final JdbcTemplate jdbcTemplate;

  private final DataSource dataSource;

  public DatabaseInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
    this.jdbcTemplate = jdbcTemplate;
    this.dataSource = dataSource;
  }

  public void initSampleData() throws Exception {
    executeScripts(SAMPLE_DATA);
  }

  public void clear() {
    jdbcTemplate.execute("TRUNCATE TABLE T_CHARGE_CATEGORY CASCADE");
    jdbcTemplate.execute("TRUNCATE TABLE T_CHARGE_DEFINITION CASCADE");
    jdbcTemplate.execute("TRUNCATE TABLE T_TARIFF_DEFINITION CASCADE");
    jdbcTemplate.execute("TRUNCATE TABLE T_CAZ_LINK_DETAIL CASCADE");
    jdbcTemplate.execute("TRUNCATE TABLE audit.logged_actions CASCADE");
  }

  private void executeScripts(List<Path> scripts) throws Exception {
    for (Path script : scripts) {
      FileSystemResource resource = new FileSystemResource(script);
      ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
    }
  }

  private static Path createTestSqlDataPath(String fileName) {
    return Paths.get("src", "it", "resources", "data", "sql", fileName);
  }
}
