package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import java.util.List;

public class IndexKeyConfiguration {
  
  private String propertyKey;
  private List<PropertyIndex> extractions;
  public String getPropertyKey() {
    return propertyKey;
  }
  public void setPropertyKey(String propertyKey) {
    this.propertyKey = propertyKey;
  }
  public List<PropertyIndex> getExtractions() {
    return extractions;
  }
  public void setExtractions(List<PropertyIndex> extractions) {
    this.extractions = extractions;
  }
}
