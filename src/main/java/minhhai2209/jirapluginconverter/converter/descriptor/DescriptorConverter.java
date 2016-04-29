package minhhai2209.jirapluginconverter.converter.descriptor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.WorkflowPostFuntion;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.connect.descriptor.websection.WebSection;
import minhhai2209.jirapluginconverter.converter.utils.XmlUtils;
import minhhai2209.jirapluginconverter.plugin.descriptor.*;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class DescriptorConverter {

  private static ObjectMapper mapper = new ObjectMapper();
  private static WebItemConverter webItemConverter = new WebItemConverter();
  private static WebPanelConverter webPanelConverter = new WebPanelConverter();
  private static WebSectionConverter webSectionConverter = new WebSectionConverter();
  private static PageConverter generalPageConverter = new PageConverter("system.top.navigation.bar");
  private static PageConverter adminPageConverter = new PageConverter("advanced_menu_section/advanced_section");
  private static IssueTabPanelConverter issueTabPanelConverter = new IssueTabPanelConverter();
  private static ProjectTabPanelConverter projectTabPanelConverter = new ProjectTabPanelConverter();
  private static WorkflowPostFunctionConverter workflowPostFunctionConverter = new WorkflowPostFunctionConverter();

  public static String convert(Modules modules) {
    try {
      StringWriter writer = new StringWriter();

      List<WebItem> webItems = modules.getWebItems();
      if (webItems != null) {
        for (WebItem webItem : webItems) {
          WebItemModule pluginModule = webItemConverter.toPluginModule(webItem, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<WebPanel> webPanels = modules.getWebPanels();
      if (webPanels != null) {
        for (WebPanel webPanel : webPanels) {
          WebPanelModule pluginModule = webPanelConverter.toPluginModule(webPanel, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<WebSection> webSections = modules.getWebSections();
      if (webSections != null) {
        for (WebSection webSection : webSections) {
          WebSectionModule pluginModule = webSectionConverter.toPluginModule(webSection, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<Page> generalPages = modules.getGeneralPages();
      if (generalPages != null) {
        for (Page page : generalPages) {
          WebItemModule pluginModule = generalPageConverter.toPluginModule(page, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<Page> adminPages = modules.getAdminPages();
      if (adminPages != null) {
        for (Page page : adminPages) {
          WebItemModule pluginModule = adminPageConverter.toPluginModule(page, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<TabPanel> jiraIssueTabPanels = modules.getJiraIssueTabPanels();
      if (jiraIssueTabPanels != null) {
        for (TabPanel tabPanel : jiraIssueTabPanels) {
          IssueTabPanelModule pluginModule = issueTabPanelConverter.toPluginModule(tabPanel, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<TabPanel> projectTabPanels = modules.getJiraProjectTabPanels();
      if (projectTabPanels != null) {
        for (TabPanel tabPanel : projectTabPanels) {
          ProjectTabPanelModule pluginModule = projectTabPanelConverter.toPluginModule(tabPanel, modules);
          XmlUtils.toXml(pluginModule, writer);
        }
      }

      List<WorkflowPostFuntion> jiraWorkflowPostFunctions = modules.getJiraWorkflowPostFunctions();
      if (jiraWorkflowPostFunctions != null) {
        for (WorkflowPostFuntion workflowPostFuntion : jiraWorkflowPostFunctions) {
          WorkflowPostFunctionModule workflowPostFunctionModule = workflowPostFunctionConverter.toPluginModule(workflowPostFuntion, modules);
          XmlUtils.toXml(workflowPostFunctionModule, writer);
        }
      }

      return writer.toString();
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  public static Descriptor analyze(String descriptorFile)
      throws IOException, JsonParseException, JsonMappingException {
    Descriptor descriptor = mapper.readValue(descriptorFile, Descriptor.class);
    return descriptor;
  }

  public static String convertPluginInfoXml(Descriptor descriptor) {
    String pluginInfoXml = "";
    // enable license
    if (descriptor.isEnableLicensing()) {
      pluginInfoXml += "<param name=\"atlassian-licensing-enabled\">true</param>";
    }
    return pluginInfoXml;
  }

}
