package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Icon {

  @XmlAttribute
  private Integer width;
  @XmlAttribute
  private Integer height;
  private String link;
  
  public Icon() {
  }
  
  public Icon(minhhai2209.jirapluginconverter.connect.descriptor.Icon icon) {
    this.height = icon.getHeight();
    this.width = icon.getWidth();
    this.link = icon.getUrl();
  }
  public Integer getWidth() {
    return width;
  }
  public void setWidth(Integer width) {
    this.width = width;
  }
  public Integer getHeight() {
    return height;
  }
  public void setHeight(Integer height) {
    this.height = height;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

}
