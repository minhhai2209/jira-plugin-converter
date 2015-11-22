package minhhai2209.jirapluginconverter.converter.descriptor;

import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions;
import minhhai2209.jirapluginconverter.plugin.descriptor.Icon;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebItemModule;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions.Type;

public class PageConverter extends ModuleConverter<WebItemModule, Page>{

  private ConditionConverter conditionConverter = new ConditionConverter();

  private String defaultLocation;

  public PageConverter(String defaultLocation) {
    this.defaultLocation = defaultLocation;
  }

  @Override
  public WebItemModule toPluginModule(Page page) {
    WebItemModule module = new WebItemModule();
    String key = page.getKey();
    String location = page.getLocation() == null ? defaultLocation : page.getLocation();
    module.setKey(key);
    module.setLabel(page.getName().getValue());
    module.setLink(getPageUrl(key));
    module.setSection(location);
    module.setWeight(page.getWeight());
    if (page.getIcon() != null) {
      module.setIcon(new Icon(page.getIcon()));
    }

    if (page.getConditions() != null) {
      ConditionWrapper conditionWrapper = page.getConditions().get(0);
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

  public static String getPageUrl(String key) {
    return "/plugins/servlet/${project.groupId}-${project.artifactId}/page/" + key + "?" + queryContext;
  }
}
