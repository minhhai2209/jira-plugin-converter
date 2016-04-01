package minhhai2209.jirapluginconverter.plugin.setting;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.WorkflowPostFuntion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowPostFunctionUtils {

  private static Map<String, WorkflowPostFuntion> workflowPostFunctionLookup;

  public static void buildWorkflowPostFunctionLookup() {
    Modules modules = PluginSetting.getModules();
    List<WorkflowPostFuntion> workflowPostFuntions = modules.getJiraWorkflowPostFunctions();
    workflowPostFunctionLookup = new HashMap<String, WorkflowPostFuntion>();
    if (workflowPostFuntions != null) {
      for (WorkflowPostFuntion workflowPostFuntion : workflowPostFuntions) {
        String key = workflowPostFuntion.getKey();
        workflowPostFunctionLookup.put(key, workflowPostFuntion);
      }
    }
  }

  public static WorkflowPostFuntion getWorkflowPostFuntion(String key) {
    return workflowPostFunctionLookup.get(key);
  }

}
