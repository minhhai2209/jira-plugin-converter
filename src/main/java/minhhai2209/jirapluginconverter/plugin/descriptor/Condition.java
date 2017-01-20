package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Condition {
  public Condition() {
    this.clazz = CONDITION_CLASS;
  }

  public Condition(String name) {
    switch(name) {
      case "user_is_logged_in":
        this.clazz = "com.atlassian.jira.plugin.webfragment.conditions.UserLoggedInCondition";
        break;
      default:
        this.clazz = CONDITION_CLASS;
    }
  }

  private static String CONDITION_CLASS = "minhhai2209.jirapluginconverter.plugin.condition.RemoteCondition";
  
  @XmlAttribute(name="class")
  private String clazz;
  
  @XmlElement(name="param")
  private List<Param> params;
  
  
  @XmlAttribute
  private boolean invert = false;

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public List<Param> getParams() {
    return params;
  }

  public void setParams(List<Param> params) {
    this.params = params;
  }

  public boolean isInvert() {
    return invert;
  }

  public void setInvert(boolean invert) {
    this.invert = invert;
  }
}
