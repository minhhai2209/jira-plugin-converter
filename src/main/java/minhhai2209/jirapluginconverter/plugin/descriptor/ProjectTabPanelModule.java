package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name="project-tabpanel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectTabPanelModule {

  @XmlAttribute
  private String key;

  @XmlAttribute
  private String name;

  @XmlAttribute(name="class")
  private String clazz;

  private Label label;

  private Description description;

  private int order;

  @XmlElement(name="resource")
  private List<Resource> resources;

  @XmlElement(name="param")
  private List<Param> params;

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

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public Description getDescription() {
    return description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public List<Resource> getResources() {
    return resources;
  }

  public void setResources(List<Resource> resources) {
    this.resources = resources;
  }

  public List<Param> getParams() {
    return params;
  }

  public void setParams(List<Param> params) {
    this.params = params;
  }
}
