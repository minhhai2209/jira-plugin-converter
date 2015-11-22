package minhhai2209.jirapluginconverter.converter.descriptor;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;
import minhhai2209.jirapluginconverter.connect.descriptor.websection.WebSection;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebSectionModule;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions.Type;

public class WebSectionConverter extends Converter<WebSectionModule, WebSection> {

  private ConditionConverter conditionConverter = new ConditionConverter();
  
  @Override
  public WebSectionModule toPluginModule(WebSection webSection) {
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
        List<Condition> clauses =conditionConverter.getConditionModules(conditionWrapper.getOr());
        conditions.setConditions(clauses);
        module.setConditions(conditions);
      } else if (conditionWrapper.getAnd() != null) {
        Conditions conditions =  new Conditions();
        conditions.setType(Type.OR);
        List<Condition> clauses = conditionConverter.getConditionModules(conditionWrapper.getAnd());
        conditions.setConditions(clauses);
        module.setConditions(conditions);
      } else {
        Condition singleCondtion = conditionConverter.toPluginModule(conditionWrapper);
        module.setCondition(singleCondtion);
      }
    };
    return module;
  }

}
