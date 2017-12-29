package minhhai2209.jirapluginconverter.converter.descriptor;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions.Type;
import minhhai2209.jirapluginconverter.plugin.descriptor.DefaultWebPanelResource;
import minhhai2209.jirapluginconverter.plugin.descriptor.Resource;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebPanelModule;

public class WebPanelConverter extends ModuleConverter<WebPanelModule, WebPanel>{

  private ConditionConverter conditionConverter = new ConditionConverter();

  @Override
  public WebPanelModule toPluginModule(WebPanel webPanel, Modules modules) {
    WebPanelModule module = new WebPanelModule();
    module.setKey(webPanel.getKey());
    module.setLabel(webPanel.getName().getValue());
    module.setLocation(webPanel.getLocation());
    module.setWeight(webPanel.getWeight());
    Resource resource = new DefaultWebPanelResource(webPanel.getKey());
    module.setResource(resource);

    if (webPanel.getConditions() != null) {
      for ( ConditionWrapper conditionWrapper : webPanel.getConditions() ) {
        if (conditionWrapper.getOr() != null) {
          Conditions conditions = new Conditions();
          conditions.setType(Type.OR);
          List<Condition> clauses = conditionConverter.getConditionModules(conditionWrapper.getOr(), modules);
          conditions.setConditions(clauses);
          module.setConditions(conditions);
        } else if (conditionWrapper.getAnd() != null) {
          Conditions conditions = new Conditions();
          conditions.setType(Type.AND);
          List<Condition> clauses = conditionConverter.getConditionModules(conditionWrapper.getAnd(), modules);
          conditions.setConditions(clauses);
          module.setConditions(conditions);
        } else {
          Condition singleCondtion = conditionConverter.toPluginModule(conditionWrapper, modules);
          module.setCondition(singleCondtion);
        }
      }
    };

    return module;
  }

}
