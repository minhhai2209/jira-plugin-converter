package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="issue-tabpanel")
public class IssueTabPanelModule {

  private String key;

  private String clazz;

  private String label;

  private boolean supportsAjaxLoad;

  @XmlAttribute
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @XmlAttribute(name="class")
  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  @XmlElement
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @XmlElement(name="supports-ajax-load")
  public boolean isSupportsAjaxLoad() {
    return supportsAjaxLoad;
  }

  public void setSupportsAjaxLoad(boolean supportsAjaxLoad) {
    this.supportsAjaxLoad = supportsAjaxLoad;
  }
}
