package minhhai2209.jirapluginconverter.connect.descriptor.page;

import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.AbstractModule;
import minhhai2209.jirapluginconverter.connect.descriptor.Icon;

public class Page extends AbstractModule {
  private String url;
  private Icon icon;
  private Map<String, String> params;
  private int weight = 100;
  
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public Icon getIcon() {
    return icon;
  }
  public void setIcon(Icon icon) {
    this.icon = icon;
  }
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
}
