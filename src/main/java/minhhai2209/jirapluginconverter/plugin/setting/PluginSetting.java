package minhhai2209.jirapluginconverter.plugin.setting;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.license.PluginLicenseManager;

import minhhai2209.jirapluginconverter.connect.descriptor.Context;
import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.connect.descriptor.LifeCycle;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.authentication.Authentication;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget.Type;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.plugin.jwt.SharedSecretGenerator;
import minhhai2209.jirapluginconverter.plugin.utils.EnumUtils;
import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class PluginSetting {

  public static final String GROUP_ID = "generated_group_id";

  public static final String ARTIFACT_ID = "generated_artifact_id";

  public static final String PLUGIN_KEY = GROUP_ID + "." + ARTIFACT_ID;

  public static final String URL_SAFE_PLUGIN_KEY = GROUP_ID + "-" + ARTIFACT_ID;

  private static Descriptor descriptor;

  private static Map<String, WebItem> webItemLookup;

  private static Map<String, WebPanel> webPanelLookup;

  private static Map<String, Page> generalPageLookup;

  private static Map<String, Page> adminPageLookup;

  private static String clientKey;

  private static String publicKey;

  private static String sharedSecret;

  private static String sen;

  public static void load(
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      PluginLicenseManager pluginLicenseManager) throws Exception {
    readDescriptor();
    loadJiraConsumer();
    sharedSecret = SharedSecretGenerator.generateSharedSecret(pluginSettingsFactory, transactionTemplate);
  }

//  private static void loadSen(PluginLicenseManager pluginLicenseManager) {
//    Option<PluginLicense> licenseOption = pluginLicenseManager.getLicense();
//    PluginLicense license = licenseOption.getOrElse((PluginLicense) null);
//    if (license == null) {
//      sen = "";
//    } else {
//      sen = license.getSupportEntitlementNumber().getOrElse("");
//    }
//  }

  private static void readDescriptor() {
    InputStream is = null;
    try {
      is = PluginSetting.class.getResourceAsStream("/imported_atlas_connect_descriptor.json");
      String descriptorString = IOUtils.toString(is);
      descriptor = JsonUtils.fromJson(descriptorString, Descriptor.class);
      buildWebItemLookup();
      buildWebPanelLookup();
      buildGeneralPageLookup();
      buildAdminPageLookup();
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

  private static void buildWebItemLookup() {
    Modules modules = descriptor.getModules();
    List<WebItem> webItems = modules.getWebItems();
    webItemLookup = new HashMap<String, WebItem>();
    if (webItems != null) {
      for (WebItem webItem : webItems) {
        String key = webItem.getKey();
        webItemLookup.put(key, webItem);
      }
    }
  }

  private static void buildWebPanelLookup() {
    Modules modules = descriptor.getModules();
    List<WebPanel> webPanels = modules.getWebPanels();
    webPanelLookup = new HashMap<String, WebPanel>();
    if (webPanels != null) {
      for (WebPanel webPanel : webPanels) {
        String key = webPanel.getKey();
        webPanelLookup.put(key, webPanel);
      }
    }
  }

  private static void buildGeneralPageLookup() {
    Modules modules = descriptor.getModules();
    List<Page> pages = modules.getGeneralPages();
    generalPageLookup = new HashMap<String, Page>();
    if (pages != null) {
      for (Page page : pages) {
        String key = page.getKey();
        generalPageLookup.put(key, page);
      }
    }
  }

  private static void buildAdminPageLookup() {
    Modules modules = descriptor.getModules();
    List<Page> pages = modules.getAdminPages();
    adminPageLookup = new HashMap<String, Page>();
    if (pages != null) {
      for (Page page : pages) {
        String key = page.getKey();
        adminPageLookup.put(key, page);
      }
    }
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

  public static String getSen() {
    return sen;
  }

  public static WebItem getWebItem(String key) {
    return webItemLookup.get(key);
  }

  public static WebPanel getWebPanel(String key) {
    return webPanelLookup.get(key);
  }

  public static Page getGeneralPage(String key) {
    return generalPageLookup.get(key);
  }

  public static Page getAdminPage(String key) {
    return adminPageLookup.get(key);
  }

  public static String getUrl(WebItem webItem) {
    String webItemUrl = webItem.getUrl();
    if (webItemUrl.startsWith("http://") || webItemUrl.startsWith("https://")) {
      return webItemUrl;
    }
    Context context = webItem.getContext();
    if (context == null) {
      context = Context.addon;
    }
    WebItemTarget target = webItem.getTarget();
    Type type;
    if (target == null) {
      type = Type.page;
    } else {
      type = target.getType();
    }
    if (type == null) {
      type = Type.page;
    }
    String baseUrl;
    if (EnumUtils.equals(type, Type.page)) {
      switch (context) {
        case addon:
        case ADDON:
          baseUrl = descriptor.getBaseUrl();
          break;
        case product:
        case PRODUCT:
          baseUrl = getJiraBaseUrl();
          break;
        case page:
        case PAGE:
          baseUrl = getJiraBaseUrl() + "/plugins/servlet/" + URL_SAFE_PLUGIN_KEY + "/page/";
          break;
        default:
          throw new IllegalStateException();
      }
    } else {
      switch (context) {
        case page:
        case PAGE:
          baseUrl = descriptor.getBaseUrl() + "/";
          break;
        default:
          baseUrl = descriptor.getBaseUrl();
      }
    }
    String url = baseUrl + webItemUrl;
    return url;
  }

  public static String getJiraBaseUrl() {
    return ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
  }

  public static String getUrl(WebPanel webPanel) {
    String baseUrl = descriptor.getBaseUrl();
    String webPanelUrl = webPanel.getUrl();
    String url = baseUrl + webPanelUrl;
    return url;
  }

  public static String getUrl(Page page) {
    String baseUrl = descriptor.getBaseUrl();
    String pageUrl = page.getUrl();
    String url = baseUrl + pageUrl;
    return url;
  }

  public static void setPublicKey(String publicKey) {
    PluginSetting.publicKey = publicKey;
  }

  public static void setClientKey(String clientKey) {
    PluginSetting.clientKey = clientKey;
  }

  public static void loadJiraConsumer() throws Exception {
    String baseJiraUrl = getJiraBaseUrl();
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
    String pubKey = publicKeyElement.getFirstChild().getNodeValue().trim();
    setPublicKey(pubKey);
    NodeList keyList = doc.getElementsByTagName("key");
    Element keyElement = (Element) keyList.item(0);
    String clientKey = keyElement.getFirstChild().getNodeValue().trim();
    setClientKey(clientKey);
  }

  public static boolean needsAuthentication() {
    Authentication authentication = descriptor.getAuthentication();
    return authentication != null &&
        EnumUtils.equals(authentication.getType(), minhhai2209.jirapluginconverter.connect.descriptor.authentication.Type.jwt);
  }

  public static String getInstalledUrl() {
    String installedUri = getInstalledUri();
    return installedUri == null ? null : descriptor.getBaseUrl() + installedUri;
  }

  public static String getInstalledUri() {
    LifeCycle lifeCycle = descriptor.getLifecycle();
    String installedUri = lifeCycle == null ? null : lifeCycle.getInstalled();
    return installedUri;
  }
}
