package minhhai2209.jirapluginconverter.converter.descriptor;

import com.atlassian.jira.plugin.workflow.JiraWorkflowPluginConstants;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.WorkflowPostFuntion;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.plugin.descriptor.*;

public class WorkflowPostFunctionConverter extends ModuleConverter<WorkflowPostFunctionModule, WorkflowPostFuntion>{
    @Override
    public WorkflowPostFunctionModule toPluginModule(WorkflowPostFuntion workflowPostFuntion, Modules modules) {
        WorkflowPostFunctionModule module = new WorkflowPostFunctionModule();
        module.setKey(workflowPostFuntion.getKey());
        module.setName(workflowPostFuntion.getName().getValue());
        module.setClazz("minhhai2209.jirapluginconverter.plugin.workflow.WorkflowPluginFactory");
        module.setDescription(new Description(workflowPostFuntion.getDescription().getValue()));
        module.setFunctionClass(new FunctionClass("minhhai2209.jirapluginconverter.plugin.workflow.JiraFunctionProvider"));
        module.setOrderable(true);
        module.setUnique(true);
        module.setDeletable(true);

        //Set the resources
        Resource viewResource = new Resource();
        viewResource.setType("velocity");
        viewResource.setName(JiraWorkflowPluginConstants.RESOURCE_NAME_VIEW);
        viewResource.setLocation("templates/workflow-show.vm");

        Resource inputParametersResource = new Resource();
        inputParametersResource.setType("velocity");
        inputParametersResource.setName(JiraWorkflowPluginConstants.RESOURCE_NAME_INPUT_PARAMETERS);
        inputParametersResource.setLocation("templates/workflow-post-function.vm");

        Resource editParametersResource = new Resource();
        editParametersResource.setType("velocity");
        editParametersResource.setName(JiraWorkflowPluginConstants.RESOURCE_NAME_EDIT_PARAMETERS);
        editParametersResource.setLocation("templates/workflow-post-function.vm");

        module.addResource(viewResource);
        module.addResource(inputParametersResource);
        module.addResource(editParametersResource);

        return module;
    }
}
