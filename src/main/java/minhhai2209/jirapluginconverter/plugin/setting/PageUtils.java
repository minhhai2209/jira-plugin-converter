package minhhai2209.jirapluginconverter.plugin.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;

public class PageUtils {

  private static Map<String, Page> generalPageLookup;

  private static Map<String, Page> adminPageLookup;

  public static String getUrl(Page page) {
    String baseUrl = PluginSetting.getPluginBaseUrl();
    String pageUrl = page.getUrl();
    String url = baseUrl + pageUrl;
    return url;
  }

  public static void buildGeneralPageLookup() {
    Modules modules = PluginSetting.getModules();
    List<Page> pages = modules.getGeneralPages();
    generalPageLookup = new HashMap<String, Page>();
    if (pages != null) {
      for (Page page : pages) {
        String key = page.getKey();
        generalPageLookup.put(key, page);
      }
    }
  }

  public static void buildAdminPageLookup() {
    Modules modules = PluginSetting.getModules();
    List<Page> pages = modules.getAdminPages();
    adminPageLookup = new HashMap<String, Page>();
    if (pages != null) {
      for (Page page : pages) {
        String key = page.getKey();
        adminPageLookup.put(key, page);
      }
    }
  }

  public static Page getGeneralPage(String key) {
    return generalPageLookup.get(key);
  }

  public static Page getAdminPage(String key) {
    return adminPageLookup.get(key);
  }
}
