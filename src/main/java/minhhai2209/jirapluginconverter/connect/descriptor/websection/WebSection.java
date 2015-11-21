package minhhai2209.jirapluginconverter.connect.descriptor.websection;

import java.util.Map;

import minhhai2209.jirapluginconverter.connect.descriptor.AbstractModule;
import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;

public class WebSection extends AbstractModule {

  private I18nProperty tooltip;
  private Map<String, String> params;
  private int weight = 100;
  public I18nProperty getTooltip() {
    return tooltip;
  }
  public void setTooltip(I18nProperty tooltip) {
    this.tooltip = tooltip;
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
