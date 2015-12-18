package minhhai2209.jirapluginconverter.plugin.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;

public class WebPanelUtils {

  private static Map<String, WebPanel> webPanelLookup;

  public static void buildWebPanelLookup() {
    Modules modules = PluginSetting.getModules();
    List<WebPanel> webPanels = modules.getWebPanels();
    webPanelLookup = new HashMap<String, WebPanel>();
    if (webPanels != null) {
      for (WebPanel webPanel : webPanels) {
        String key = webPanel.getKey();
        webPanelLookup.put(key, webPanel);
      }
    }
  }

  public static String getFullUrl(WebPanel webPanel) {

    String baseUrl = PluginSetting.getPluginBaseUrl();
    String webPanelUrl = webPanel.getUrl();
    String url = baseUrl + webPanelUrl;
    return url;
  }

  public static WebPanel getWebPanel(String key) {
    return webPanelLookup.get(key);
  }

}
