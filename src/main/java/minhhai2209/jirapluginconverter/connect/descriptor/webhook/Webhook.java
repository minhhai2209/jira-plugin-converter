package minhhai2209.jirapluginconverter.connect.descriptor.webhook;

import java.util.List;
import java.util.Map;

public class Webhook {

  private String event;
  private String url;
  private Map<String, String> params;
  private boolean excludeBody;
  private List<String> propertyKeys;
  
  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public boolean isExcludeBody() {
    return excludeBody;
  }

  public void setExcludeBody(boolean excludeBody) {
    this.excludeBody = excludeBody;
  }

  public List<String> getPropertyKeys() {
    return propertyKeys;
  }

  public void setPropertyKeys(List<String> propertyKeys) {
    this.propertyKeys = propertyKeys;
  }
}
