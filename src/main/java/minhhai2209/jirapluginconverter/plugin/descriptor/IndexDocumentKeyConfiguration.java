package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="key")
public class IndexDocumentKeyConfiguration {

  private String propertyKey;

  private List<IndexDocumentKeyExtractConfiguration> extractions;

  @XmlAttribute(name="property-key")
  public String getPropertyKey() {
    return propertyKey;
  }

  public void setPropertyKey(String propertyKey) {
    this.propertyKey = propertyKey;
  }

  @XmlElement(name="extract")
  public List<IndexDocumentKeyExtractConfiguration> getExtractions() {
    return extractions;
  }

  public void setExtractions(List<IndexDocumentKeyExtractConfiguration> extractions) {
    this.extractions = extractions;
  }
}
