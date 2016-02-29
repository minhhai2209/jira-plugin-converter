package minhhai2209.jirapluginconverter.plugin.descriptor;

import minhhai2209.jirapluginconverter.plugin.condition.RemoteCondition;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Condition {

  private static String CONDITION_CLASS = RemoteCondition.class.getName();
  
  @XmlAttribute(name="class")
  private String clazz = CONDITION_CLASS;
  
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
