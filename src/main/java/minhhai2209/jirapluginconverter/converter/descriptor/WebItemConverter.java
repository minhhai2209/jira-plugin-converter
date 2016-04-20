package minhhai2209.jirapluginconverter.converter.descriptor;

import java.util.ArrayList;
import java.util.List;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.condition.ConditionWrapper;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget;
import minhhai2209.jirapluginconverter.connect.descriptor.websection.WebSection;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions;
import minhhai2209.jirapluginconverter.plugin.descriptor.Conditions.Type;
import minhhai2209.jirapluginconverter.plugin.descriptor.Icon;
import minhhai2209.jirapluginconverter.plugin.descriptor.Link;
import minhhai2209.jirapluginconverter.plugin.descriptor.Param;
import minhhai2209.jirapluginconverter.plugin.descriptor.WebItemModule;

public class WebItemConverter extends ModuleConverter<WebItemModule, WebItem>{

  private ConditionConverter conditionConverter = new ConditionConverter();

  @Override
  public WebItemModule toPluginModule(WebItem webItem, Modules modules) {
    WebItemModule module = new WebItemModule();
    String key = webItem.getKey();
    module.setKey(key);
    module.setLabel(webItem.getName().getValue());
    module.setLink(getWebItemUrl(key, modules));
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
          List<Param> params = new ArrayList<Param>();
          Param param = new Param();
          param.setName("-acopt-chrome");
          param.setValue("false");
          params.add(param);
          module.setParams(params);
          module.setStyleClass("ap-dialog ap-plugin-key-${project.artifactId} ap-module-key-" + key);
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

  public static Link getWebItemUrl(String key, Modules modules) {
    Link link = new Link();
    boolean menu = false;
    List<WebSection> sections = modules.getWebSections();
    for (WebSection section : sections) {
      if (key.equals(section.getLocation())) {
        menu = true;
      }
    }
    if (menu) {
      link.setLinkId(key);
    } else {
      String queryContext = "projectKey=${project.key}" +
          "&projectId=${project.id}" +
          "&versionId=${version.id}" +
          "&componentId=${component.id}" +
          "&issueId=${issue.id}" +
          "&issueKey=${issue.key}";
      String pluginKey = "${project.artifactId}";
      link.setValue("/plugins/servlet/ac/" + pluginKey + "/" + pluginKey + "__" + key + "?" + queryContext);
    }
    return link;
  }
}
