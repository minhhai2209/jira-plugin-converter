package generated_group_id.utils.jwt;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="consumer")
public class Consumer {

  private String key;

  private String name;

  private String publicKey;

  private String description;

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

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
