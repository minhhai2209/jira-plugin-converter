package minhhai2209.jirapluginconverter.plugin.setting;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectTabPanelUtils {

  private static Map<String, TabPanel> projectTabPanelLookup;

  public static String getFullUrl(TabPanel tabPanel) {
    String baseUrl = PluginSetting.getPluginBaseUrl();
    String pageUrl = tabPanel.getUrl();
    String url = baseUrl + pageUrl;
    return url;
  }

  public static TabPanel getProjectTabPanel(String key) {
    TabPanel tabPanel = projectTabPanelLookup.get(key);
    return tabPanel;
  }

  public static void buildProjectTabPanelLookup() {
    Modules modules = PluginSetting.getModules();
    List<TabPanel> tabPanels = modules.getJiraProjectTabPanels();
    projectTabPanelLookup = new HashMap<String, TabPanel>();
    if (tabPanels != null) {
      for (TabPanel tabPanel : tabPanels) {
        String key = tabPanel.getKey();
        projectTabPanelLookup.put(key, tabPanel);
      }
    }
  }
}
