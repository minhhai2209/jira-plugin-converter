package minhhai2209.jirapluginconverter.connect.descriptor;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;

public abstract class AbstractModule {

  protected String key;
  protected I18nProperty name;
  protected String location;
  protected List<ConditionWrapper> conditions;
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public I18nProperty getName() {
    return name;
  }
  public void setName(I18nProperty name) {
    this.name = name;
  }
  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    this.location = location;
  }
  public List<ConditionWrapper> getConditions() {
    return conditions;
  }
  public void setConditions(List<ConditionWrapper> conditions) {
    this.conditions = conditions;
  }
}
