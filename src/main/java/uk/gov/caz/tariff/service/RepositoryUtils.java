package uk.gov.caz.tariff.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

/**
 * Contains helper methods common to Tariff-API repositories.
 */
@UtilityClass
public class RepositoryUtils {

  /**
   * Given results set, takes date value and returns String representation.
   *
   * @param rs Result set.
   * @param columnName String column name in database
   * @return String representation of date.
   * @throws SQLException if date was malformed.
   */
  public static String safelyGetDate(ResultSet rs, String columnName) throws SQLException {
    String dateTime = "";
    Date dateValueDt = rs.getDate(columnName);
    if (dateValueDt != null) {
      LocalDate localDate = dateValueDt.toLocalDate();
      dateTime = localDate.format(DateTimeFormatter.ISO_DATE);
    }
    return dateTime;
  }
}
