package minhhai2209.jirapluginconverter.plugin.lifecycle;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.license.PluginLicenseManager;

import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.JiraUtils;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.LifeCycleUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.setting.SenUtils;
import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class PluginLifeCycleEventHandler {

  private PluginSettingsFactory pluginSettingsFactory;

  private TransactionTemplate transactionTemplate;

  private ApplicationProperties applicationProperties;

  private PluginLicenseManager pluginLicenseManager;

  private ConsumerService consumerService;

  private String jiraVersion;

  private String pluginVersion;

  public PluginLifeCycleEventHandler(
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      ApplicationProperties applicationProperties,
      PluginLicenseManager pluginLicenseManager,
      ConsumerService consumerService) {

    this.pluginSettingsFactory = pluginSettingsFactory;
    this.transactionTemplate = transactionTemplate;
    this.applicationProperties = applicationProperties;
    this.pluginLicenseManager = pluginLicenseManager;
    this.consumerService = consumerService;
  }

  public void onInstalled(StringBuilder error) throws Exception {

    Plugin plugin = PluginSetting.getJiraPlugin();

    JiraUtils.setApplicationProperties(applicationProperties);

    jiraVersion = applicationProperties.getVersion();
    PluginSetting.load(pluginSettingsFactory, transactionTemplate, pluginLicenseManager, consumerService);

    String existingSharedSecret = KeyUtils.getSharedSecret();
    if (existingSharedSecret == null) {
      KeyUtils.generateSharedSecret(pluginSettingsFactory, transactionTemplate);
    }
    String currentSharedSecret = KeyUtils.getSharedSecret();

    pluginVersion = plugin.getPluginInformation().getVersion();
    String uri = LifeCycleUtils.getInstalledUri();
    String jwt = (existingSharedSecret == null) ? null :
      JwtComposer.compose(KeyUtils.getClientKey(), existingSharedSecret, "POST", uri, null, null);

    notify(error, EventType.installed, uri, currentSharedSecret, jwt);
  }

  public void onEnabled() throws Exception {
    PluginSetting.load(pluginSettingsFactory, transactionTemplate, pluginLicenseManager, consumerService);

    String uri = LifeCycleUtils.getEnabledUri();
    String jwt = JwtComposer.compose(KeyUtils.getClientKey(), KeyUtils.getSharedSecret(), "POST", uri, null, null);
    notify(null, EventType.enabled, uri, null, jwt);
  }

  public void onDisabled() throws Exception {
    String uri = LifeCycleUtils.getDisabledUri();
    String jwt = JwtComposer.compose(KeyUtils.getClientKey(), KeyUtils.getSharedSecret(), "POST", uri, null, null);
    notify(null, EventType.disabled, uri, null, jwt);
  }

  public void onUninstalled() throws Exception {
    String uri = LifeCycleUtils.getUninstalledUri();
    String jwt = JwtComposer.compose(KeyUtils.getClientKey(), KeyUtils.getSharedSecret(), "POST", uri, null, null);
    notify(null, EventType.uninstalled, uri, null, jwt);
  }

  private void notify(StringBuilder error, EventType eventType, String uri, String sharedSecret, String jwt) throws Exception {
    try {
      if (uri != null) {
        PluginLifeCycleEvent event = new PluginLifeCycleEvent();
        event.setBaseUrl(JiraUtils.getFullBaseUrl());
        event.setClientKey(KeyUtils.getClientKey());
        event.setDescription("");
        event.setEventType(eventType);
        event.setKey(PluginSetting.getDescriptor().getKey());
        event.setPluginsVersion(pluginVersion);
        event.setProductType(ProductType.jira);
        event.setPublicKey(KeyUtils.getPublicKey());
        event.setServerVersion(jiraVersion);
        event.setServiceEntitlementNumber(SenUtils.getSen());
        event.setSharedSecret(sharedSecret);
        notify(uri, event, jwt);
      }
    } catch (Exception e) {
      System.out.println(PluginSetting.getDescriptor().getKey() + " PLUGIN NOTIFY EVENT '" +
        eventType.toString() + "' FAILED: " + e.getMessage());
      if (error != null) {
        error.append(ExceptionUtils.getStackTrace(e));
      }
    }
  }

  private void notify(String uri, PluginLifeCycleEvent event, String jwt) {
    try {
      String url = getUrl(uri);
      if (url != null) {
        HttpClient httpClient = HttpClientFactory.build();
        HttpPost post = new HttpPost(url);
        String json = JsonUtils.toJson(event);
        post.setEntity(new StringEntity(json));
        post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        if (jwt != null) {
          post.addHeader("Authorization", "JWT " + jwt);
        }
        httpClient.execute(post);
      }
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private static String getUrl(String uri) {
    String url;
    if (uri == null) {
      url = null;
    } else {
      String pluginBaseUrl = PluginSetting.getPluginBaseUrl();
      if (pluginBaseUrl == null) {
        url = null;
      } else {
        url = pluginBaseUrl + uri;
      }
    }
    return url;
  }
}
