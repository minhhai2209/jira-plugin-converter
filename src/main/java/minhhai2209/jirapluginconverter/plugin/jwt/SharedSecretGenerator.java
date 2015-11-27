package minhhai2209.jirapluginconverter.plugin.jwt;

import java.util.UUID;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;

public class SharedSecretGenerator {

  public static String generateSharedSecret(
      final PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate) {

    String sharedSecret = transactionTemplate.execute(new TransactionCallback<String>() {

      @Override
      public String doInTransaction() {
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        String settingKey = PluginSetting.PLUGIN_KEY + ".sharedSecret";
        String sharedSecret = (String) settings.get(settingKey);
        if (sharedSecret == null) {
          sharedSecret = UUID.randomUUID().toString();
          settings.put(settingKey, sharedSecret);
        }
        return sharedSecret;
      }
    });
    return sharedSecret;
  }

}
