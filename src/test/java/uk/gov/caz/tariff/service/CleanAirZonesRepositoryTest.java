package uk.gov.caz.tariff.service;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.caz.definitions.dto.CleanAirZoneDto;
import uk.gov.caz.definitions.dto.CleanAirZonesDto;
import uk.gov.caz.tariff.service.CleanAirZonesRepository.CleanAirZoneRowMapper;

@ExtendWith(MockitoExtension.class)
class CleanAirZonesRepositoryTest {

  private static final UUID SOME_CLEAN_AIR_ZONE_ID = UUID
      .fromString("5dd5c926-ed33-4a0a-b911-46324433e866");

  private static final String SOME_URL = "www.test.uk";
  private static final String EXEMPTION_URL = "www.exemption.uk";
  private static final String MAIN_INFO_URL = "www.main.info";
  private static final String FLEETS_COMPLIANCE_URL = "www.fleets-compliance.info";
  private static final String PAYMENTS_COMPLIANCE_URL = "www.payments-compliance.info";
  private static final String PRIVACY_POLICY_URL = "www.policy.info";

  private static final String BATH = "Bath";
  private static final String BATH_OPERATOR_NAME = "Bath and North East Somerset Council";

  private static LocalDate ACTIVE_CHARGE_START_DATE = LocalDate.of(2018, 10, 28);
  private static LocalDate DISPLAY_FROM = LocalDate.of(2021, 3, 15);;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private CleanAirZonesRepository cleanAirZonesRepository;

  @Test
  public void shouldFindCleanAirZones() {
    // given
    List<CleanAirZoneDto> cleanAirZones = mockCleanAirZonesInDB();

    // when
    CleanAirZonesDto result = cleanAirZonesRepository.findAll();

    // then
    assertThat(result.getCleanAirZones())
        .hasSize(2)
        .contains(cleanAirZones.get(0), cleanAirZones.get(1));
  }

  @Test
  public void shouldReturnEmptyList() {
    // given

    // when
    CleanAirZonesDto result = cleanAirZonesRepository.findAll();

    // then
    assertThat(result.getCleanAirZones()).isEmpty();
  }

  @Nested
  class RowMapper {

    private CleanAirZoneRowMapper rowMapper = new CleanAirZoneRowMapper();

    @Test
    public void shouldMapResultSetToTariff() throws SQLException {
      ResultSet resultSet = mockResultSet();

      CleanAirZoneDto cleanAirZone = rowMapper.mapRow(resultSet, 0);

      assertThat(cleanAirZone)
          .isNotNull()
          .isEqualToComparingFieldByFieldRecursively(expectedCleanAirZone());
    }

    private ResultSet mockResultSet() throws SQLException {
      ResultSet resultSet = mock(ResultSet.class);
      when(resultSet.getObject("clean_air_zone_id", UUID.class))
          .thenReturn(SOME_CLEAN_AIR_ZONE_ID);

      when(resultSet.getDate("active_charge_start_time"))
          .thenReturn(Date.valueOf(ACTIVE_CHARGE_START_DATE));

      when(resultSet.getDate("display_from")).thenReturn(Date.valueOf(DISPLAY_FROM));

      when(resultSet.getString(anyString())).thenAnswer(answer -> {
        String argument = answer.getArgument(0);
        switch (argument) {
          case "caz_name":
            return BATH;
          case "boundary_url":
            return SOME_URL;
          case "exemption_url":
            return EXEMPTION_URL;
          case "main_info_url":
            return MAIN_INFO_URL;
          case "payments_compliance_url":
            return PAYMENTS_COMPLIANCE_URL;
          case "privacy_policy_url":
            return PRIVACY_POLICY_URL;
          case "fleets_compliance_url":
            return FLEETS_COMPLIANCE_URL;
          case "caz_operator_name":
            return BATH_OPERATOR_NAME;
          case "active_charge_start_date_text":
            return "15 March 2021";
          case "display_order":
            return 1;
        }
        throw new RuntimeException("Value not stubbed!");
      });

      return resultSet;
    }
  }

  private List<CleanAirZoneDto> mockCleanAirZonesInDB() {
    List<CleanAirZoneDto> cleanAirZones = prepareCleanAirZones();
    when(jdbcTemplate.query(anyString(), any(CleanAirZoneRowMapper.class)))
        .thenReturn(cleanAirZones);
    return cleanAirZones;
  }

  private List<CleanAirZoneDto> prepareCleanAirZones() {
    return CleanAirZonesDto.builder().cleanAirZones(
        newArrayList(
            caz("Birmingham", "0d7ab5c4-5fff-4935-8c4e-56267c0c9493",
                "https://www.birmingham.gov.uk/info/20076/pollution/"
                    + "1763/a_clean_air_zone_for_birmingham/3",
                "https://exemptions.birmingham.gov.uk", MAIN_INFO_URL, FLEETS_COMPLIANCE_URL,
                PAYMENTS_COMPLIANCE_URL, PRIVACY_POLICY_URL, ACTIVE_CHARGE_START_DATE,
                "1 June 2021", DISPLAY_FROM, 2,
                "Birmingham City Council", false),

            caz("Bath", "5dd5c926-ed33-4a0a-b911-46324433e866",
                "http://www.bathnes.gov.uk/zonemaps",
                "http://www.bathnes.gov.uk/CAZexemptions", MAIN_INFO_URL, FLEETS_COMPLIANCE_URL,
                PAYMENTS_COMPLIANCE_URL, PRIVACY_POLICY_URL, ACTIVE_CHARGE_START_DATE,
                "15 March 2021", DISPLAY_FROM, 1,
                "Bath and North East Somerset Council", true)
        )).build().getCleanAirZones();
  }

  private CleanAirZoneDto caz(String cazName, String cleanAirZoneId, String boundaryUrl,
      String exemptionUrl, String mainInfoUrl, String fleetsComplianceUrl,
      String paymentsComplianceUrl, String privacyPolicyUrl, LocalDate activeChargeStartDate,
      String activeChargeStartDateText, LocalDate displayFrom, Integer displayOrder,
      String operatorName, boolean directDebitEnabled) {
    return CleanAirZoneDto.builder()
        .name(cazName)
        .cleanAirZoneId(UUID.fromString(cleanAirZoneId))
        .boundaryUrl(URI.create(boundaryUrl))
        .exemptionUrl(URI.create(exemptionUrl))
        .mainInfoUrl(URI.create(mainInfoUrl))
        .fleetsComplianceUrl(URI.create(fleetsComplianceUrl))
        .paymentsComplianceUrl(URI.create(paymentsComplianceUrl))
        .privacyPolicyUrl(URI.create(privacyPolicyUrl))
        .activeChargeStartDate(activeChargeStartDate.format(DateTimeFormatter.ISO_DATE))
        .activeChargeStartDateText(activeChargeStartDateText)
        .displayFrom(displayFrom.format(DateTimeFormatter.ISO_DATE))
        .displayOrder(displayOrder)
        .operatorName(operatorName)
        .directDebitEnabled(directDebitEnabled)
        .build();
  }

  private CleanAirZoneDto expectedCleanAirZone() {
    return CleanAirZoneDto.builder()
        .cleanAirZoneId(SOME_CLEAN_AIR_ZONE_ID)
        .name(BATH)
        .operatorName(BATH_OPERATOR_NAME)
        .boundaryUrl(URI.create(SOME_URL))
        .exemptionUrl(URI.create(EXEMPTION_URL))
        .mainInfoUrl(URI.create(MAIN_INFO_URL))
        .paymentsComplianceUrl(URI.create(PAYMENTS_COMPLIANCE_URL))
        .privacyPolicyUrl(URI.create(PRIVACY_POLICY_URL))
        .fleetsComplianceUrl(URI.create(FLEETS_COMPLIANCE_URL))
        .activeChargeStartDate(ACTIVE_CHARGE_START_DATE.format(DateTimeFormatter.ISO_DATE))
        .activeChargeStartDateText("15 March 2021")
        .displayFrom(DISPLAY_FROM.format(DateTimeFormatter.ISO_DATE))
        .build();
  }
}