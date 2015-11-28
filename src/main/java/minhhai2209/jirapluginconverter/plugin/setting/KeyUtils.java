package minhhai2209.jirapluginconverter.plugin.setting;

import java.util.UUID;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Strings;

public class KeyUtils {

  private static String clientKey;

  private static String publicKey;

  private static String sharedSecret;

  public static void generateSharedSecret(
      final PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate) {

    sharedSecret = transactionTemplate.execute(new TransactionCallback<String>() {

      @Override
      public String doInTransaction() {
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        String settingKey = getSharedSecretSettingKey();
        String sharedSecret = (String) settings.get(settingKey);
        if (sharedSecret == null) {
          sharedSecret = UUID.randomUUID().toString();
          settings.put(settingKey, sharedSecret);
        }
        return sharedSecret;
      }
    });
  }

  public static void deleteSharedSecret(
      final PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate) {

    sharedSecret = null;
    transactionTemplate.execute(new TransactionCallback<Void>() {

      @Override
      public Void doInTransaction() {
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        String settingKey = getSharedSecretSettingKey();
        settings.remove(settingKey);
        return null;
      }
    });
  }

  public static void loadJiraConsumer(ConsumerService consumerService) throws Exception {
    Consumer consumer = consumerService.getConsumer();
    clientKey = Strings.nullToEmpty(consumer.getKey());
    publicKey = Strings.nullToEmpty(RSAKeys.toPemEncoding(consumer.getPublicKey()));
  }

  public static String getClientKey() {
    return clientKey;
  }

  public static String getPublicKey() {
    return publicKey;
  }

  public static String getSharedSecret() {
    return sharedSecret;
  }

  private static String getSharedSecretSettingKey() {
    String settingKey = PluginSetting.PLUGIN_KEY + ".sharedSecret";
    return settingKey;
  }
}
