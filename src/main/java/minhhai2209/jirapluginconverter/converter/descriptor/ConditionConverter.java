package minhhai2209.jirapluginconverter.converter.descriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.plugin.descriptor.Condition;
import minhhai2209.jirapluginconverter.plugin.descriptor.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConditionConverter extends ModuleConverter<Condition, minhhai2209.jirapluginconverter.connect.descriptor.condition.Condition>{

  @Override
  public Condition toPluginModule(minhhai2209.jirapluginconverter.connect.descriptor.condition.Condition connectCondition, Modules modules) {
    Condition conditionModule = new Condition(connectCondition.getCondition());
    conditionModule.setInvert(connectCondition.isInvert());
    List<Param> clauses = new ArrayList<Param>();

    Map<String, String> params = connectCondition.getParams();
    if (params != null) {
      for (String key : params.keySet()) {
        clauses.add(new Param(key, params.get(key)));
      }
    }
    conditionModule.setParams(clauses);
    return conditionModule;
  }

  public List<Condition> getConditionModules(
      List<minhhai2209.jirapluginconverter.connect.descriptor.condition.Condition> connectConditions,
      Modules modules) {
    List<Condition> conditions = new ArrayList<Condition>();
    for (minhhai2209.jirapluginconverter.connect.descriptor.condition.Condition connectCondition : connectConditions) {
      conditions.add(toPluginModule(connectCondition, modules));
    }
    return conditions;
  }

}
