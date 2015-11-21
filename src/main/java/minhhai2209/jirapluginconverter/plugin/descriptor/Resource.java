package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource {

  @XmlAttribute
  protected String name;
  @XmlAttribute
  protected  String namePattern;
  @XmlAttribute
  protected String type;
  @XmlAttribute
  protected String location;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getNamePattern() {
    return namePattern;
  }
  public void setNamePattern(String namePattern) {
    this.namePattern = namePattern;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    this.location = location;
  }
}
