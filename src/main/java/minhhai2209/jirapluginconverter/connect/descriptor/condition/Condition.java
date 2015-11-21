package minhhai2209.jirapluginconverter.connect.descriptor.condition;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Condition {

  private String condition;
  private Map<String, String> params;
  private boolean invert = false;
  public String getCondition() {
    return condition;
  }
  public void setCondition(String condition) {
    this.condition = condition;
  }
  public Map<String, String> getParams() {
    return params;
  }
  public void setParams(Map<String, String> params) {
    this.params = params;
  }
  public boolean isInvert() {
    return invert;
  }
  public void setInvert(boolean invert) {
    this.invert = invert;
  }
}
