package minhhai2209.jirapluginconverter.connect.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class I18nProperty {

  private String value;
  private String i18n;
  
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public String getI18n() {
    return i18n;
  }
  public void setI18n(String i18n) {
    this.i18n = i18n;
  }
}
