package minhhai2209.jirapluginconverter.connect.descriptor.webitem;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import minhhai2209.jirapluginconverter.connect.descriptor.AbstractModule;
import minhhai2209.jirapluginconverter.connect.descriptor.Context;
import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;
import minhhai2209.jirapluginconverter.connect.descriptor.Icon;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WebItem extends AbstractModule {

  private Context context;
  private I18nProperty tooltip;
  private Icon icon;
  private Map<String, String> params;
  private List<String> styleClasses;
  private WebItemTarget target;
  private int weight = 100;
  private String url;


  public Context getContext() {
    return context;
  }
  public void setContext(Context context) {
    this.context = context;
  }
  public I18nProperty getTooltip() {
    return tooltip;
  }
  public void setTooltip(I18nProperty tooltip) {
    this.tooltip = tooltip;
  }
  public Icon getIcon() {
    return icon;
  }
  public void setIcon(Icon icon) {
    this.icon = icon;
  }
  public Map<String, String> getParams() {
    return params;
  }
  public void setParams(Map<String, String> params) {
    this.params = params;
  }
  public List<String> getStyleClasses() {
    return styleClasses;
  }
  public void setStyleClasses(List<String> styleClasses) {
    this.styleClasses = styleClasses;
  }
  public WebItemTarget getTarget() {
    return target;
  }
  public void setTarget(WebItemTarget target) {
    this.target = target;
  }
  public int getWeight() {
    return weight;
  }
  public void setWeight(int weight) {
    this.weight = weight;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
}
