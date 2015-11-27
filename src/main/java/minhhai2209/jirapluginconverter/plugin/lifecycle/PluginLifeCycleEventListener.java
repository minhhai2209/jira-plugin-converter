package minhhai2209.jirapluginconverter.plugin.lifecycle;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.license.PluginLicenseManager;

import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.LifeCycleUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.setting.SenUtils;
import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class PluginLifeCycleEventListener implements InitializingBean, DisposableBean {

  private EventPublisher eventPublisher;

  private PluginSettingsFactory pluginSettingsFactory;

  private TransactionTemplate transactionTemplate;

  private ApplicationProperties applicationProperties;

  private PluginLicenseManager pluginLicenseManager;

  public PluginLifeCycleEventListener(
      EventPublisher eventPublisher,
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      ApplicationProperties applicationProperties,
      PluginLicenseManager pluginLicenseManager) {
    this.eventPublisher = eventPublisher;
    this.pluginSettingsFactory = pluginSettingsFactory;
    this.transactionTemplate = transactionTemplate;
    this.applicationProperties = applicationProperties;
    this.pluginLicenseManager = pluginLicenseManager;
  }

  @Override
  public void destroy() throws Exception {
    eventPublisher.unregister(this);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    eventPublisher.register(this);
  }

  @PluginEventListener
  public void onPluginEnabled(PluginEnabledEvent enabledEvent) throws Exception {
    Plugin enabledPlugin = enabledEvent.getPlugin();
    String enabledPluginKey = enabledPlugin.getKey();
    if (PluginSetting.PLUGIN_KEY.equals(enabledPluginKey)) {
      PluginSetting.load(pluginSettingsFactory, transactionTemplate, pluginLicenseManager);
      String installedUrl = LifeCycleUtils.getInstalledUrl();
      if (installedUrl != null) {

        PluginLifeCycleEvent event = new PluginLifeCycleEvent();
        event.setBaseUrl(PluginSetting.getJiraBaseUrl());
        event.setClientKey(KeyUtils.getClientKey());
        event.setDescription("");
        event.setEventType(EventType.enabled);
        event.setKey(PluginSetting.PLUGIN_KEY);
        event.setPluginsVersion(enabledPlugin.getPluginInformation().getVersion());
        event.setProductType(ProductType.jira);
        event.setPublicKey(KeyUtils.getPublicKey());
        event.setServerVersion(applicationProperties.getVersion());
        event.setServiceEntitlementNumber(SenUtils.getSen());
        event.setSharedSecret(KeyUtils.getSharedSecret());

        sendInstall(installedUrl, event);
      }
    }
  }

  private void sendInstall(String installedUrl, PluginLifeCycleEvent event) {
    try {
      String installedUri = LifeCycleUtils.getInstalledUri();
      HttpClient httpClient = HttpClientFactory.build();
      HttpPost post = new HttpPost(installedUrl);
      String json = JsonUtils.toJson(event);
      post.setEntity(new StringEntity(json));
      post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
      String jwt = JwtComposer.compose(KeyUtils.getClientKey(), KeyUtils.getSharedSecret(), "POST", installedUri, null, null);
      post.addHeader("Authorization", "JWT " + jwt);
      httpClient.execute(post);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
}
