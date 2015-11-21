package minhhai2209.jirapluginconverter.connect.descriptor.webhook;

import java.util.Map;

public class Webhook {

  public String event;
  public String url;
  public Map<String, String> params;
  
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
}
