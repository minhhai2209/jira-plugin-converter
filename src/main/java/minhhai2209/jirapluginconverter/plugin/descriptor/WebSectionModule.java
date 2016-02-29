package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="web-section")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSectionModule {

  @XmlAttribute
  private String clazz;
  @XmlAttribute(required=true)
  private String key;
  @XmlAttribute
  private String name;
  @XmlAttribute(required=true)
  private String location;
  @XmlAttribute(required=true)
  private int weight;
  
  private Condition condition;
  private Conditions conditions;
  
  private Label label = new Label();

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getLabel() {
    return this.label.getKey();
  }

  public void setLabel(String label) {
    this.label.setKey(label);
  }

  public Condition getCondition() {
    return condition;
  }

  public void setCondition(Condition condition) {
    this.condition = condition;
  }

  public Conditions getConditions() {
    return conditions;
  }

  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
  }
}
