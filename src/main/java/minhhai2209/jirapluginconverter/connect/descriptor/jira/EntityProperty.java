package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;

import java.util.List;

public class EntityProperty {

  private I18nProperty name;
  private String key;
  private EntityType entityType;
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

  public EntityType getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }

  public List<IndexKeyConfiguration> getKeyConfigurations() {
    return keyConfigurations;
  }

  public void setKeyConfigurations(List<IndexKeyConfiguration> keyConfigurations) {
    this.keyConfigurations = keyConfigurations;
  }
}
