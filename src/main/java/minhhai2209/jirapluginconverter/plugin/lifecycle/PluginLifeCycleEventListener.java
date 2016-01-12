package minhhai2209.jirapluginconverter.plugin.lifecycle;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
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

public class PluginLifeCycleEventListener implements InitializingBean, DisposableBean {

  private PluginEventManager pluginEventManager;

  private PluginSettingsFactory pluginSettingsFactory;

  private TransactionTemplate transactionTemplate;

  private ApplicationProperties applicationProperties;

  private PluginLicenseManager pluginLicenseManager;

  private ConsumerService consumerService;

  private String jiraVersion;

  private String pluginVersion;

  public PluginLifeCycleEventListener(
      PluginEventManager pluginEventManager,
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      ApplicationProperties applicationProperties,
      PluginLicenseManager pluginLicenseManager,
      ConsumerService consumerService) {

    this.pluginEventManager = pluginEventManager;
    this.pluginSettingsFactory = pluginSettingsFactory;
    this.transactionTemplate = transactionTemplate;
    this.applicationProperties = applicationProperties;
    this.pluginLicenseManager = pluginLicenseManager;
    this.consumerService = consumerService;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    pluginEventManager.register(this);
    jiraVersion = applicationProperties.getVersion();
    PluginSetting.load(pluginSettingsFactory, transactionTemplate, pluginLicenseManager, consumerService);
    JiraUtils.setApplicationProperties(applicationProperties);
  }

  @PluginEventListener
  public void onPluginEnabled(PluginEnabledEvent event) throws Exception {
    if (event == null) {
      return;
    }
    Plugin plugin = event.getPlugin();
    if (plugin == null) {
      return;
    }
    String pluginKey = plugin.getKey();
    if (!PluginSetting.PLUGIN_KEY.equals(pluginKey)) {
      return;
    }
    pluginVersion = plugin.getPluginInformation().getVersion();
    notify(EventType.installed, LifeCycleUtils.getInstalledUri());
  }

  private void notify(EventType eventType, String uri) throws Exception {
    try {
      if (uri != null) {
        PluginLifeCycleEvent event = new PluginLifeCycleEvent();
        event.setBaseUrl(JiraUtils.getFullBaseUrl());
        event.setClientKey(KeyUtils.getClientKey());
        event.setDescription("");
        event.setEventType(eventType);
        event.setKey(PluginSetting.PLUGIN_KEY);
        event.setPluginsVersion(pluginVersion);
        event.setProductType(ProductType.jira);
        event.setPublicKey(KeyUtils.getPublicKey());
        event.setServerVersion(jiraVersion);
        event.setServiceEntitlementNumber(SenUtils.getSen());
        event.setSharedSecret(KeyUtils.getSharedSecret());
        notify(uri, event);
      }
    } catch (Exception e) {

    }
  }

  private void notify(String uri, PluginLifeCycleEvent event) {
    try {
      String url = getUrl(uri);
      HttpClient httpClient = HttpClientFactory.build();
      HttpPost post = new HttpPost(url);
      String json = JsonUtils.toJson(event);
      post.setEntity(new StringEntity(json));
      post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      String jwt = JwtComposer.compose(KeyUtils.getClientKey(), KeyUtils.getSharedSecret(), "POST", uri, null, null);
      post.addHeader("Authorization", "JWT " + jwt);
      httpClient.execute(post);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private static String getUrl(String uri) {
    return uri == null ? null : PluginSetting.getPluginBaseUrl() + uri;
  }

  @Override
  public void destroy() throws Exception {
    pluginEventManager.unregister(this);
    notify(EventType.uninstalled, LifeCycleUtils.getUninstalledUri());
  }
}
