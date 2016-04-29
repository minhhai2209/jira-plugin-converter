package minhhai2209.jirapluginconverter.plugin.setting;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueTabPanelUtils {

  private static Map<String, TabPanel> jiraIssueTabPanelLookup;

  public static String getFullUrl(TabPanel tabPanel) {
    String baseUrl = PluginSetting.getPluginBaseUrl();
    String pageUrl = tabPanel.getUrl();
    String url = baseUrl + pageUrl;
    return url;
  }

  public static TabPanel getJiraIssueTabPanel(String key) {
    TabPanel tabPanel = jiraIssueTabPanelLookup.get(key);
    return tabPanel;
  }

  public static void buildJiraIssueTabPanelLookup() {
    Modules modules = PluginSetting.getModules();
    List<TabPanel> tabPanels = modules.getJiraIssueTabPanels();
    jiraIssueTabPanelLookup = new HashMap<String, TabPanel>();
    if (tabPanels != null) {
      for (TabPanel tabPanel : tabPanels) {
        String key = tabPanel.getKey();
        jiraIssueTabPanelLookup.put(key, tabPanel);
      }
    }
  }
}
