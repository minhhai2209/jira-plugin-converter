package minhhai2209.jirapluginconverter.plugin.setting;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;

public class JiraUtils {

  private static ApplicationProperties applicationProperties;

  public static void setApplicationProperties(ApplicationProperties applicationProperties) {
    JiraUtils.applicationProperties = applicationProperties;
  }

  public static String getBaseUrl() {
    String fullBaseUrl = getFullBaseUrl();
    String contextPath = getContextPath();
    String baseUrl;
    if (contextPath.isEmpty()) {
      baseUrl = fullBaseUrl;
    } else {
      int index = fullBaseUrl.lastIndexOf(contextPath);
      baseUrl = fullBaseUrl.substring(0, index);
    }
    return baseUrl;
  }

  public static String getFullBaseUrl() {
    return applicationProperties.getBaseUrl(UrlMode.CANONICAL);
  }

  public static String getContextPath() {
    return applicationProperties.getBaseUrl(UrlMode.RELATIVE_CANONICAL);
  }
}
