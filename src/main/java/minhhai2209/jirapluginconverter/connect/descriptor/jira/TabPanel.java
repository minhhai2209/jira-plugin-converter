package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;
import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;

public class TabPanel {

  private I18nProperty name;
  private String key;
  private String url;
  private ConditionWrapper conditions;
  private Map<String, String> params;
  private int weight =100;
  public I18nProperty getName() {
    return name;
  }
  public void setName(I18nProperty name) {
    this.name = name;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public ConditionWrapper getConditions() {
    return conditions;
  }
  public void setConditions(ConditionWrapper conditions) {
    this.conditions = conditions;
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
