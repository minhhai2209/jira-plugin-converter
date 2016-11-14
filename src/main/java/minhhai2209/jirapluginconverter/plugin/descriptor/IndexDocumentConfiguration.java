package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="index-document-configuration")
public class IndexDocumentConfiguration {

  private String entityKey;

  private String key;

  private List<IndexDocumentKeyConfiguration> keyConfigurations;

  @XmlAttribute(name="entity-key")
  public String getEntityKey() {
    return entityKey;
  }

  public void setEntityKey(String entityKey) {
    this.entityKey = entityKey;
  }

  @XmlAttribute
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @XmlElement(name="key")
  public List<IndexDocumentKeyConfiguration> getKeyConfigurations() {
    return keyConfigurations;
  }

  public void setKeyConfigurations(List<IndexDocumentKeyConfiguration> keyConfigurations) {
    this.keyConfigurations = keyConfigurations;
  }
}
