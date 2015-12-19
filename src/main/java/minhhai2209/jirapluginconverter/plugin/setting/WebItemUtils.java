package minhhai2209.jirapluginconverter.plugin.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.Context;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget.Type;
import minhhai2209.jirapluginconverter.plugin.utils.EnumUtils;

public class WebItemUtils {

  private static Map<String, WebItem> webItemLookup;

  public static void buildWebItemLookup() {
    Modules modules = PluginSetting.getModules();
    List<WebItem> webItems = modules.getWebItems();
    webItemLookup = new HashMap<String, WebItem>();
    if (webItems != null) {
      for (WebItem webItem : webItems) {
        String key = webItem.getKey();
        webItemLookup.put(key, webItem);
      }
    }
  }

  public static String getFullUrl(WebItem webItem) {
    String webItemUrl = webItem.getUrl();
    if (webItemUrl.startsWith("http://") || webItemUrl.startsWith("https://")) {
      return webItemUrl;
    }
    Context context = webItem.getContext();
    if (context == null) {
      context = Context.addon;
    }
    WebItemTarget target = webItem.getTarget();
    Type type;
    if (target == null) {
      type = Type.page;
    } else {
      type = target.getType();
    }
    if (type == null) {
      type = Type.page;
    }
    String baseUrl;
    if (EnumUtils.equals(type, Type.page)) {
      switch (context) {
        case addon:
        case ADDON:
          baseUrl = PluginSetting.getPluginBaseUrl();
          break;
        case product:
        case PRODUCT:
          baseUrl = JiraUtils.getJiraBaseUrl();
          break;
        case page:
        case PAGE:
          baseUrl = JiraUtils.getJiraBaseUrl() + "/plugins/servlet/" + PluginSetting.URL_SAFE_PLUGIN_KEY + "/page/";
          break;
        default:
          throw new IllegalStateException();
      }
    } else {
      switch (context) {
        case page:
        case PAGE:
          baseUrl = PluginSetting.getPluginBaseUrl() + "/";
          break;
        default:
          baseUrl = PluginSetting.getPluginBaseUrl();
      }
    }
    String url = baseUrl + webItemUrl;
    return url;
  }

  public static WebItem getWebItem(String key) {
    return webItemLookup.get(key);
  }
}
