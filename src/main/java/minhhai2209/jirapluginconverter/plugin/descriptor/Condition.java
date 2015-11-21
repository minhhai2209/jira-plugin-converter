package minhhai2209.jirapluginconverter.plugin.descriptor;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Condition {

  private static String CONDITION_CLASS = "${project.groupId}.plugin.condition";
  
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
