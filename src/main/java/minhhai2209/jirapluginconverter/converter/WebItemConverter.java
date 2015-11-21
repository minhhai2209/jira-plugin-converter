package minhhai2209.jirapluginconverter.converter;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions;
import minhhai2209.jirapluginconverter.plugin.descriptor.Icon;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebItemModule;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions.Type;

public class WebItemConverter extends Converter<WebItemModule, WebItem>{
  
  private ConditionConverter conditionConverter = new ConditionConverter();

  @Override
  public WebItemModule toPluginModule(WebItem webItem) {
    WebItemModule module = new WebItemModule();
    String key = webItem.getKey();
    module.setKey(key);
    module.setLabel(webItem.getName().getValue());
    module.setLink(getWebItemUrl(key));
    module.setSection(webItem.getLocation());
    module.setWeight(webItem.getWeight());
    if (webItem.getIcon() != null) {
      module.setIcon(new Icon(webItem.getIcon()));
    }
    
    if (webItem.getTarget() != null) {
      WebItemTarget.Type type = webItem.getTarget().getType();
      switch (type) {
        case DIALOG:
        case dialog:
          module.setStyleClass("trigger-dialog");
          break;
        default:
          break;
      }
    }
    
    if (webItem.getConditions() != null) {
      ConditionWrapper conditionWrapper = webItem.getConditions().get(0);
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
  
  public static String getWebItemUrl(String key) {
    return "/plugins/servlet/${project.groupId}-${project.artifactId}/web-item/" + key + "?" + queryContext;
  }
}
