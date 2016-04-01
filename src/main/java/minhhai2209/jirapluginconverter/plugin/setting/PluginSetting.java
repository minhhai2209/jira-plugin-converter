package minhhai2209.jirapluginconverter.plugin.setting;

import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.license.PluginLicenseManager;
import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class PluginSetting {

  public static final String GROUP_ID = "generated_group_id";

  public static final String ARTIFACT_ID = "generated_artifact_id";

  public static final String PLUGIN_KEY = ARTIFACT_ID;

  public static final String URL_SAFE_PLUGIN_KEY = GROUP_ID + "-" + ARTIFACT_ID;

  private static Descriptor descriptor;

  private static Plugin plugin;

  public static void load(
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      PluginLicenseManager pluginLicenseManager,
      ConsumerService consumerService) throws Exception {
    readDescriptor();
    LicenseUtils.setPluginLicenseManager(pluginLicenseManager);
    KeyUtils.loadJiraConsumer(consumerService);
    KeyUtils.loadSharedSecret(pluginSettingsFactory);
  }

  private static void readDescriptor() {
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
      TabPanelUtils.buildJiraIssueTabPanelLookup();

      plugin = new Plugin();
      plugin.setName(descriptor.getName());
      plugin.setBaseUrl(getPluginBaseUrl());
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
    return descriptor.getBaseUrl();
  }

  public static Modules getModules() {
    return descriptor.getModules();
  }

  public static Plugin getPlugin() {
    return plugin;
  }
}
