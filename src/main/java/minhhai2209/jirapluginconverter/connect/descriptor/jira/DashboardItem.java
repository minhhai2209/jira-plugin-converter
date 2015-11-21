package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;
import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;

public class DashboardItem {

  private I18nProperty description;
  private I18nProperty name;
  private String key;
  private String thumbnailUrl;
  private String url;
  private ConditionWrapper conditions;
  private boolean configurable = false;
  private Map<String, String> params;
  public I18nProperty getDescription() {
    return description;
  }
  public void setDescription(I18nProperty description) {
    this.description = description;
  }
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
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }
  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
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
  public boolean isConfigurable() {
    return configurable;
  }
  public void setConfigurable(boolean configurable) {
    this.configurable = configurable;
  }
  public Map<String, String> getParams() {
    return params;
  }
  public void setParams(Map<String, String> params) {
    this.params = params;
  };
  
  
}
