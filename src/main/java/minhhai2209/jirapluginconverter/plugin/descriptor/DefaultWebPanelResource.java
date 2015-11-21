package minhhai2209.jirapluginconverter.plugin.descriptor;

public class DefaultWebPanelResource extends Resource {

  
  public DefaultWebPanelResource(String location) {
    this.location = location;
    this.name = "view";
    this.type = "${project.groupId}.${project.artifactId}-iframe";
  }
}
