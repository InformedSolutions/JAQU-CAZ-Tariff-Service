package uk.gov.caz.tariff.amazonaws;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.google.common.base.Splitter;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uk.gov.caz.tariff.Application;
import uk.gov.caz.tariff.service.CsvImporterService;
import uk.gov.caz.tariff.service.CsvImporterService.NewCsvEvent;

public class SnsLambdaHandler implements RequestHandler<SNSEvent, String> {

  private static final Splitter SPLITTER = Splitter.on(',')
      .trimResults()
      .omitEmptyStrings();

  private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
  private static CsvImporterService csvImporterService;

  static {
    try {
      String listOfActiveSpringProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
      if (listOfActiveSpringProfiles != null) {
        handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class,
            splitToArray(listOfActiveSpringProfiles));
      } else {
        handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
      }
      csvImporterService = WebApplicationContextUtils
          .getWebApplicationContext(handler.getServletContext()).getBean(CsvImporterService.class);
    } catch (ContainerInitializationException e) {
      // if we fail here. We re-throw the exception to force another cold start
      e.printStackTrace();
      throw new RuntimeException("Could not initialize Spring Boot application", e);
    }
  }

  private static String[] splitToArray(String activeProfiles) {
    return SPLITTER.splitToList(activeProfiles).toArray(new String[0]);
  }

  @Override
  public String handleRequest(SNSEvent snsEvent, Context context) {
    CsvImporterService.NewCsvEvent newCsvEvent = new NewCsvEvent("/s3");
    return csvImporterService.importCsv(newCsvEvent);
  }
}
