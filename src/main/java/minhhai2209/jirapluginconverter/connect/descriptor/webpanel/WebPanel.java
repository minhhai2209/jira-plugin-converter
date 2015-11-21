package minhhai2209.jirapluginconverter.connect.descriptor.webpanel;

import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.AbstractModule;
import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;

public class WebPanel extends AbstractModule {
  private Map<String, String> params;
  private int weight = 100;
  private WebPanelLayout layout;
  protected String url;
  private I18nProperty tooltip;

  public Map<String, String> getParams() {
    return params;
  }
  public void setParams(Map<String, String> params) {
    this.params = params;
  }
  public int getWeight() {
    return weight;
  }
  public void setWeight(int weight) {
    this.weight = weight;
  }
  public WebPanelLayout getLayout() {
    return layout;
  }
  public void setLayout(WebPanelLayout layout) {
    this.layout = layout;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public I18nProperty getTooltip() {
    return tooltip;
  }
  public void setTooltip(I18nProperty tooltip) {
    this.tooltip = tooltip;
  }
}
