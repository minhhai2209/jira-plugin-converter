package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="web-item")
public class WebItemModule {

  private String clazz;

  private String key;

  private String name;

  private String section;

  private int weight;

  private Label label = new Label();

  private Link link;

  private Icon icon;

  private String styleClass;

  private Condition condition;
  private Conditions conditions;

  public Icon getIcon() {
    return icon;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @XmlAttribute(name="class")
  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  @XmlAttribute
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @XmlAttribute
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlAttribute
  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  @XmlAttribute
  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  @XmlElement
  public String getLabel() {
    return this.label.getKey();
  }

  public void setLabel(String label) {
    this.label.setKey(label);
  }

  @XmlElement
  public Link getLink() {
    return link;
  }

  public void setLink(Link link) {
    this.link = link;
  }

  @XmlElement
  public String getStyleClass() {
    return styleClass;
  }

  public void setStyleClass(String styleClass) {
    this.styleClass = styleClass;
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

  public void setLabel(Label label) {
    this.label = label;
  }

}
