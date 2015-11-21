package minhhai2209.jirapluginconverter.connect.descriptor.jira;

public class PropertyIndex {

  public static enum Type {
    number, NUMBER, text, TEXT, string, STRING, date, DATE
  }
  
  private String objectName;
  private Type type;
  private String alias;
  public String getObjectName() {
    return objectName;
  }
  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }
  public Type getType() {
    return type;
  }
  public void setType(Type type) {
    this.type = type;
  }
  public String getAlias() {
    return alias;
  }
  public void setAlias(String alias) {
    this.alias = alias;
  }
  
}
