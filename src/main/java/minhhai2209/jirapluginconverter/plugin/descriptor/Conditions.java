package minhhai2209.jirapluginconverter.plugin.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Conditions {

  public enum Type {
    OR, AND
  }
  
  @XmlAttribute
  private Type type;
  @XmlElement(name="condition")
  private List<Condition> conditions;
  
  public Type getType() {
    return type;
  }
  public void setType(Type type) {
    this.type = type;
  }
  public List<Condition> getConditions() {
    return conditions;
  }
  public void setConditions(List<Condition> conditions) {
    this.conditions = conditions;
  }
}
