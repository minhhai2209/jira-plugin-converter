package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;

public class EntityProperty {

  private I18nProperty name;
  private String key;
  private String entityType = "issue";
  private List<IndexKeyConfiguration> keyConfigurations;
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
  public String getEntityType() {
    return entityType;
  }
  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }
  public List<IndexKeyConfiguration> getKeyConfigurations() {
    return keyConfigurations;
  }
  public void setKeyConfigurations(List<IndexKeyConfiguration> keyConfigurations) {
    this.keyConfigurations = keyConfigurations;
  }
  
}
