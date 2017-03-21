package minhhai2209.jirapluginconverter.plugin.condition;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import minhhai2209.jirapluginconverter.plugin.rest.Properties;

public class EntityPropertyContainsContextCondition implements Condition {
  
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
    
    final Object projectObject = context.get("project");
    if (projectObject != null && (projectObject instanceof Project)) {
      final Project project = (Project)projectObject;
      final List<String> projects = Properties.getEnabledProjects(this.propertyKey, this.objectName);
      if (projects != null) {
        for (final String key : projects) {
          if (project.getKey().equalsIgnoreCase(key)) {
            return true;
          }
        }
      }
    }
    return false;
  }

}
