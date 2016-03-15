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

  public static String getCreateUrl(WorkflowPostFuntion workflowPostFuntion) {
    String url = workflowPostFuntion.getCreate().getUrl();
    return url;
  }

  public static String getEditUrl(WorkflowPostFuntion workflowPostFuntion) {
    String url = workflowPostFuntion.getEdit().getUrl();
    return url;
  }

  public static String getViewUrl(WorkflowPostFuntion workflowPostFuntion) {
    String url = workflowPostFuntion.getView().getUrl();
    return url;
  }

  public static String getTriggeredUrl(WorkflowPostFuntion workflowPostFuntion){
    String url = workflowPostFuntion.getTriggered().getUrl();
    return url;
  }
  public static WorkflowPostFuntion getWorkflowPostFuntion(String key) {
    return workflowPostFunctionLookup.get(key);
  }

}
