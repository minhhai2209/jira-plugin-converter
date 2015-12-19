package minhhai2209.jirapluginconverter.plugin.setting;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

public class JiraUtils {

  public static String getJiraBaseUrl() {
    return ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
  }
}
