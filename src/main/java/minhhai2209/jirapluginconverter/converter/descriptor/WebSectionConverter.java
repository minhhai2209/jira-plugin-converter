package minhhai2209.jirapluginconverter.converter.descriptor;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;
import minhhai2209.jirapluginconverter.connect.descriptor.websection.WebSection;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions.Type;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebSectionModule;

public class WebSectionConverter extends ModuleConverter<WebSectionModule, WebSection> {

  private ConditionConverter conditionConverter = new ConditionConverter();

  @Override
  public WebSectionModule toPluginModule(WebSection webSection, Modules modules) {
    WebSectionModule module = new WebSectionModule();
    module.setKey(webSection.getKey());
    module.setLabel(webSection.getName().getValue());
    module.setLocation(webSection.getLocation());
    module.setWeight(webSection.getWeight());

    if (webSection.getConditions() != null) {
      ConditionWrapper conditionWrapper = webSection.getConditions().get(0);
      if (conditionWrapper.getOr() != null) {
        Conditions conditions =  new Conditions();
        conditions.setType(Type.OR);
        List<Condition> clauses =conditionConverter.getConditionModules(conditionWrapper.getOr(), modules);
        conditions.setConditions(clauses);
        module.setConditions(conditions);
      } else if (conditionWrapper.getAnd() != null) {
        Conditions conditions =  new Conditions();
        conditions.setType(Type.OR);
        List<Condition> clauses = conditionConverter.getConditionModules(conditionWrapper.getAnd(), modules);
        conditions.setConditions(clauses);
        module.setConditions(conditions);
      } else {
        Condition singleCondtion = conditionConverter.toPluginModule(conditionWrapper, modules);
        module.setCondition(singleCondtion);
      }
    };
    return module;
  }

}
