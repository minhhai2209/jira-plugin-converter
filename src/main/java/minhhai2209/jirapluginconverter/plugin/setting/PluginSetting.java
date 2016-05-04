package minhhai2209.jirapluginconverter.plugin.setting;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.google.common.collect.Iterables;
import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.plugin.config.ConfigurePluginServlet;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Collection;

public class PluginSetting {

//  public static final String ARTIFACT_ID = "generated_artifact_id";

//  public static final String PLUGIN_KEY = ARTIFACT_ID;

  private static Descriptor descriptor;

  private static com.atlassian.plugin.Plugin jiraPlugin;

  private static PluginSettingsFactory pluginSettingsFactory;

  private static TransactionTemplate transactionTemplate;

  public static void load(
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      PluginLicenseManager pluginLicenseManager,
      ConsumerService consumerService) throws Exception {

    PluginSetting.pluginSettingsFactory = pluginSettingsFactory;
    PluginSetting.transactionTemplate = transactionTemplate;

    LicenseUtils.setPluginLicenseManager(pluginLicenseManager);
    KeyUtils.loadJiraConsumer(consumerService);
    KeyUtils.loadSharedSecret(pluginSettingsFactory);
  }

  public static void readDescriptor() {
    InputStream is = null;
    try {
      is = PluginSetting.class.getResourceAsStream("/imported_atlas_connect_descriptor.json");
      String descriptorString = IOUtils.toString(is);
      descriptor = JsonUtils.fromJson(descriptorString, Descriptor.class);
      WebItemUtils.buildWebItemLookup();
      WebPanelUtils.buildWebPanelLookup();
      PageUtils.buildGeneralPageLookup();
      PageUtils.buildAdminPageLookup();
      PageUtils.buildConfigurePageLookup();
      IssueTabPanelUtils.buildJiraIssueTabPanelLookup();
      ProjectTabPanelUtils.buildProjectTabPanelLookup();
      WorkflowPostFunctionUtils.buildWorkflowPostFunctionLookup();
    } catch (Exception e1) {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e2) {
          ExceptionUtils.throwUnchecked(e2);
        }
      }
      ExceptionUtils.throwUnchecked(e1);
    }
  }

  public static Descriptor getDescriptor() {
    return descriptor;
  }

  public static String getPluginBaseUrl() {
    String baseUrl = transactionTemplate.execute(new TransactionCallback<String>() {

      @Override
      public String doInTransaction() {
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        String url = (String) settings.get(ConfigurePluginServlet.DB_URL);
        return url;
      }

    });
    if (baseUrl == null) {
      baseUrl = descriptor.getBaseUrl();
    }
    String jiraUrl = JiraUtils.getBaseUrl();
    if (jiraUrl.startsWith("http:") && baseUrl.startsWith("https:")) {
      baseUrl = baseUrl.replace("https:", "http:");
    } else if (jiraUrl.startsWith("https:")) {
      baseUrl = baseUrl.replace("http:", "https:");
    }
    return baseUrl;
  }

  public static Modules getModules() {
    return descriptor.getModules();
  }

  public static Plugin getPlugin() {
    Plugin plugin = new Plugin();
    plugin.setName(descriptor.getName());
    plugin.setBaseUrl(getPluginBaseUrl());
    return plugin;
  }

  public static com.atlassian.plugin.Plugin getJiraPlugin() {
    return jiraPlugin;
  }

  public static void setJiraPlugin(com.atlassian.plugin.Plugin jiraPlugin) {
    PluginSetting.jiraPlugin = jiraPlugin;
  }

  public static ApplicationUser getPluginUser() {
    String userKey = transactionTemplate.execute(new TransactionCallback<String>() {
      @Override
      public String doInTransaction() {

        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        String userKey = (String) settings.get(ConfigurePluginServlet.DB_USER);
        return userKey;
      }
    });
    ApplicationUser user;
    UserUtil userUtil = ComponentAccessor.getUserUtil();
    if (userKey != null) {
      user = userUtil.getUserByKey(userKey);
    } else {
      Collection<User> admins = userUtil.getJiraAdministrators();
      User admin = Iterables.get(admins, 0);
      String adminName = admin.getName();
      user = userUtil.getUserByName(adminName);
    }
    return user;
  }
}
