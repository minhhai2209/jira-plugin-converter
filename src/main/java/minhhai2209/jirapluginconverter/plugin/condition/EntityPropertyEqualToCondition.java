package minhhai2209.jirapluginconverter.plugin.condition;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import minhhai2209.jirapluginconverter.plugin.rest.Properties;

public class EntityPropertyEqualToCondition implements Condition {

  private Map<String, String> params;
  private String entity;
  private String propertyKey;
  private String objectName;
  private String contextParameter;

  @Override
  public void init(Map<String, String> params) throws PluginParseException {
    this.params = params;
    this.entity = params.get("entity");
    this.propertyKey = params.get("propertyKey");
    this.objectName = params.get("objectName");
    this.contextParameter = params.get("contextParameter");
  }

  @Override
  public boolean shouldDisplay(Map<String, Object> context) {
    return Properties.getPerProject(this.propertyKey, this.objectName);
  }
}
