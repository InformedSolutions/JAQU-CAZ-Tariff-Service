package uk.gov.caz.tariff;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.caz.ApplicationConfiguration;
import uk.gov.caz.tariff.configuration.RequestMappingConfiguration;
import uk.gov.caz.tariff.configuration.SwaggerConfiguration;
import uk.gov.caz.tariff.controller.CleanAirZonesController;
import uk.gov.caz.tariff.service.CleanAirZonesRepository;
import uk.gov.caz.tariff.service.TariffRepository;

@Import({
    RequestMappingConfiguration.class,
    SwaggerConfiguration.class,
    CleanAirZonesController.class,
    TariffRepository.class,
    CleanAirZonesRepository.class
})
@Configuration
public class TariffSpringConfig implements ApplicationConfiguration {
}
