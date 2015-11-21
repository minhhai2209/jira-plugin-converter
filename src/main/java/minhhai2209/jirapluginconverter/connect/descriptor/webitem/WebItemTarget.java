package minhhai2209.jirapluginconverter.connect.descriptor.webitem;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class WebItemTarget {

  public static enum Type {
    PAGE, DIALOG, INLINEDIALOG, page, dialog, inlinedialog;
  }

  private Type type;

  private Map<String, Object> options;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Map<String, Object> getOptions() {
    return options;
  }

  public void setOptions(Map<String, Object> options) {
    this.options = options;
  }

}
