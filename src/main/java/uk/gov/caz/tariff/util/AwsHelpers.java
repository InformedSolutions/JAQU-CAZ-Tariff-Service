package uk.gov.caz.tariff.util;

public final class AwsHelpers {

  /**
   * Determines if code is running locally using AWS SAM Local tool.
   *
   * @return true if code is running in Lambda environment simulated locally by AWS SAM Local tool
   *     (docker).
   */
  public static boolean areWeRunningLocallyUsingSam() {
    return System.getenv("AWS_SAM_LOCAL") != null;
  }

  /**
   * Returns value of AWS_ACCESS_KEY_ID environment variable (if set).
   *
   * @return value of AWS_ACCESS_KEY_ID environment variable if set or null otherwise.
   */
  public static String getAwsAccessKeyFromEnvVar() {
    return System.getenv("AWS_ACCESS_KEY_ID");
  }

  /**
   * Returns value of AWS_REGION environment variable (if set).
   *
   * @return value of AWS_REGION environment variable if set or null otherwise.
   */
  public static String getAwsRegionFromEnvVar() {
    return System.getenv("AWS_REGION");
  }

  /**
   * Returns value of AWS_PROFILE environment variable (if set).
   *
   * @return value of AWS_PROFILE environment variable if set or null otherwise.
   */
  public static String getAwsProfileFromEnvVar() {
    return System.getenv("AWS_PROFILE");
  }
}
