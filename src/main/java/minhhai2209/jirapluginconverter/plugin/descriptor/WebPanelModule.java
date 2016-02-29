package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="web-panel")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebPanelModule {

  @XmlAttribute
  private String clazz;

  @XmlAttribute(required=true)
  private String key;

  @XmlAttribute
  private String name;

  @XmlAttribute
  private int weight;

  @XmlAttribute(required=true)
  private String location;
  
  @XmlElement(required=true)
  private Label label = new Label();
  
  @XmlElement
  private Resource resource;

  @XmlTransient
  private Condition condition;

  @XmlTransient
  private Conditions conditions;

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

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
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

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }
}
