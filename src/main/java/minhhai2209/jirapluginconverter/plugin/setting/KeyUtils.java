package minhhai2209.jirapluginconverter.plugin.setting;

import java.io.StringReader;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;

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
        String settingKey = PluginSetting.PLUGIN_KEY + ".sharedSecret";
        String sharedSecret = (String) settings.get(settingKey);
        if (sharedSecret == null) {
          sharedSecret = UUID.randomUUID().toString();
          settings.put(settingKey, sharedSecret);
        }
        return sharedSecret;
      }
    });
  }

  public static void loadJiraConsumer() throws Exception {
    String baseJiraUrl = PluginSetting.getJiraBaseUrl();
    String url = baseJiraUrl + "/plugins/servlet/oauth/consumer-info";
    HttpClient client = HttpClientFactory.build();
    HttpGet httpGet = new HttpGet(url);
    HttpResponse response = client.execute(httpGet);
    HttpEntity entity = response.getEntity();
    String xmlString = EntityUtils.toString(entity);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = factory.newDocumentBuilder();
    InputSource inStream = new InputSource();
    inStream.setCharacterStream(new StringReader(xmlString));
    Document doc = db.parse(inStream);
    NodeList nl = doc.getElementsByTagName("publicKey");
    Element publicKeyElement = (Element) nl.item(0);
    publicKey = publicKeyElement.getFirstChild().getNodeValue().trim();
    NodeList keyList = doc.getElementsByTagName("key");
    Element keyElement = (Element) keyList.item(0);
    clientKey = keyElement.getFirstChild().getNodeValue().trim();
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
}
