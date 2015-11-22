package minhhai2209.jirapluginconverter.converter.descriptor;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.connect.descriptor.Module;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.connect.descriptor.websection.WebSection;
import minhhai2209.jirapluginconverter.converter.utils.XmlUtils;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebItemModule;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebPanelModule;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebSectionModule;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class DescriptorConverter {

  private static ObjectMapper mapper = new ObjectMapper();
  private static WebItemConverter webItemConverter = new WebItemConverter();
  private static WebPanelConverter webPanelConverter = new WebPanelConverter();
  private static WebSectionConverter webSectionConverter = new WebSectionConverter();
  private static PageConverter generalPageConverter = new PageConverter("system.top.navigation.bar");
  private static PageConverter adminPageConverter = new PageConverter("advanced_menu_section/advanced_section");

  public static String convert(File descriptorFile) {
    try {
      Descriptor descriptor = mapper.readValue(descriptorFile, Descriptor.class);
      Module modules = descriptor.getModules();
      StringWriter writer = new StringWriter();

      List<WebItem> webItems = modules.getWebItems();
      if (webItems != null) {
        for (WebItem webItem : webItems) {
          WebItemModule pluginModule = webItemConverter.toPluginModule(webItem);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<WebPanel> webPanels = modules.getWebPanels();
      if (webPanels != null) {
        for (WebPanel webPanel : webPanels) {
          WebPanelModule pluginModule = webPanelConverter.toPluginModule(webPanel);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<WebSection> webSections = modules.getWebSections();
      if (webSections != null) {
        for (WebSection webSection : webSections) {
          WebSectionModule pluginModule = webSectionConverter.toPluginModule(webSection);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<Page> generalPages = modules.getGeneralPages();
      if (generalPages != null) {
        for (Page page : generalPages) {
          WebItemModule pluginModule = generalPageConverter.toPluginModule(page);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<Page> adminPages = modules.getAdminPages();
      if (adminPages != null) {
        for (Page page : adminPages) {
          WebItemModule pluginModule = adminPageConverter.toPluginModule(page);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      return writer.toString();
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

}
