package minhhai2209.jirapluginconverter.plugin.lifecycle;

public class PluginLifeCycleEvent {

  private String key;

  private String clientKey;

  private String publicKey;

  private String sharedSecret;

  private String serverVersion;

  private String pluginsVersion;

  private String baseUrl;

  private ProductType productType;

  private String description;

  private String serviceEntitlementNumber;

  private EventType eventType;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getClientKey() {
    return clientKey;
  }

  public void setClientKey(String clientKey) {
    this.clientKey = clientKey;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getSharedSecret() {
    return sharedSecret;
  }

  public void setSharedSecret(String sharedSecret) {
    this.sharedSecret = sharedSecret;
  }

  public String getServerVersion() {
    return serverVersion;
  }

  public void setServerVersion(String serverVersion) {
    this.serverVersion = serverVersion;
  }

  public String getPluginsVersion() {
    return pluginsVersion;
  }

  public void setPluginsVersion(String pluginsVersion) {
    this.pluginsVersion = pluginsVersion;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public ProductType getProductType() {
    return productType;
  }

  public void setProductType(ProductType productType) {
    this.productType = productType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getServiceEntitlementNumber() {
    return serviceEntitlementNumber;
  }

  public void setServiceEntitlementNumber(String serviceEntitlementNumber) {
    this.serviceEntitlementNumber = serviceEntitlementNumber;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

}
