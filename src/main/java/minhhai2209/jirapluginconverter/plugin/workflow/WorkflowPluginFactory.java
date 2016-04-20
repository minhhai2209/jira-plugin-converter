package minhhai2209.jirapluginconverter.plugin.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.http.client.utils.URIBuilder;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.JiraWorkflowPluginConstants;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.message.LocaleResolver;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.jira.WorkflowPostFuntion;
import minhhai2209.jirapluginconverter.plugin.iframe.HostConfig;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.render.ParameterContextBuilder;
import minhhai2209.jirapluginconverter.plugin.setting.AuthenticationUtils;
import minhhai2209.jirapluginconverter.plugin.setting.JiraUtils;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.LicenseUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.setting.WorkflowPostFunctionUtils;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class WorkflowPluginFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {

  public static final String STORED_POSTFUNCTION_ID = "remoteWorkflowPostFunctionUUID";
  public static final String STORED_POSTFUNCTION_CONFIG = "remoteWorkflowPostFunctionConfiguration";

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public WorkflowPluginFactory(
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver) {

    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
  }

  @Override
  public Map<String, Object> getVelocityParams(final String resourceName, final AbstractDescriptor descriptor) {

    Map<String, Object> velocityParams = super.getVelocityParams(resourceName, descriptor);

    //Fix to address the issue of having a null descriptor
    WorkflowPostFuntion workflowPostFuntion = null;
    String key = null;
    if(descriptor == null){
      //Get the first workflow post function
      if(PluginSetting.getDescriptor().getModules().getJiraWorkflowPostFunctions().size() > 0){
        workflowPostFuntion = PluginSetting.getDescriptor().getModules().getJiraWorkflowPostFunctions().get(0);
        key = workflowPostFuntion.getKey();
      }
    }else{
      key = getKey( (FunctionDescriptor) descriptor);
      workflowPostFuntion = WorkflowPostFunctionUtils.getWorkflowPostFuntion(key);
    }

    String workflowPostFunctionUrl;
    if (JiraWorkflowPluginConstants.RESOURCE_NAME_VIEW.equals(resourceName)) {
      workflowPostFunctionUrl = workflowPostFuntion.getView().getUrl();
    } else if (JiraWorkflowPluginConstants.RESOURCE_NAME_INPUT_PARAMETERS.equals(resourceName)) {
      workflowPostFunctionUrl = workflowPostFuntion.getCreate().getUrl();
    } else if (JiraWorkflowPluginConstants.RESOURCE_NAME_EDIT_PARAMETERS.equals(resourceName)) {
      workflowPostFunctionUrl = workflowPostFuntion.getEdit().getUrl();
    } else {
      throw new IllegalStateException();
    }

    addContext(velocityParams, workflowPostFunctionUrl, key);

    return velocityParams;
  }

  @Override
  protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
    velocityParams.put("postFunctionId", UUID.randomUUID().toString());
  }

  @Override
  protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
    getVelocityParamsForView(velocityParams, descriptor);
  }

  @Override
  protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
    FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
    velocityParams.put("postFunctionId", functionDescriptor.getArgs().get(STORED_POSTFUNCTION_ID));
    velocityParams.put("postFunctionConfig", functionDescriptor.getArgs().get(STORED_POSTFUNCTION_CONFIG));
  }

  @Override
  public Map<String, ?> getDescriptorParams(Map<String, Object> formParams) {

    String uuid = extractSingleParam(formParams, "postFunction.id");
    String functionConfiguration = extractSingleParam(formParams, "postFunction.config-" + uuid);
    Map<String, String> descriptorParams = new HashMap<String, String>();
    descriptorParams.put(STORED_POSTFUNCTION_CONFIG, functionConfiguration);
    descriptorParams.put(STORED_POSTFUNCTION_ID, uuid);
    return descriptorParams;
  }

  private String getKey(FunctionDescriptor functionDescriptor) {
    String key = (String) functionDescriptor.getArgs().get("full.module.key");
    return key.replaceFirst(PluginSetting.PLUGIN_KEY, "");
  }

  private void addContext(Map<String, Object> velocityParams, String workflowPostFuntionUrl, String key) {
    try {

      String postFunctionId = (String) velocityParams.get("postFunctionId");

      String baseUrl = PluginSetting.getPluginBaseUrl();
      String fullUrl = baseUrl + workflowPostFuntionUrl;

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      Map<String, String> productContext = ParameterContextBuilder.buildWorkflowContext( velocityParams);

      String xdm_e = JiraUtils.getBaseUrl();
      String cp = JiraUtils.getContextPath();
      String ns = PluginSetting.getDescriptor().getKey() + "__" + key;
      String xdm_c = "channel-" + ns;
      String dlg = "";
      String simpleDlg = dlg;
      String general = "";
      String w = "";
      String h = "";
      String productCtx = JsonUtils.toJson(productContext);
      String timezone = timeZone.getID();
      String loc = LocaleUtils.getLocale(localeResolver);
      String userId = user != null ? user.getUsername() : "";
      String userKey = user != null ? user.getKey() : "";
      String lic = LicenseUtils.getLic();
      String cv = "";

      String urlWithContext = ParameterContextBuilder.buildUrl(fullUrl, productContext);

      URIBuilder uriBuilder = new URIBuilder(urlWithContext)
          .addParameter("tz", timezone)
          .addParameter("loc", loc)
          .addParameter("user_id", userId)
          .addParameter("user_key", userKey)
          .addParameter("xdm_e", xdm_e)
          .addParameter("xdm_c", xdm_c)
          .addParameter("cp", cp)
          .addParameter("lic", lic)
          .addParameter("cv", cv);

      if (AuthenticationUtils.needsAuthentication()) {
        String jwt = JwtComposer.compose(
            KeyUtils.getClientKey(),
            KeyUtils.getSharedSecret(),
            "GET",
            uriBuilder,
            userKey,
            workflowPostFuntionUrl);
        uriBuilder.addParameter("jwt", jwt);
      }
      String url = uriBuilder.toString();

      HostConfig hostConfig = new HostConfig();
      hostConfig.setNs(ns);
      hostConfig.setKey(PluginSetting.getDescriptor().getKey() + "__" + postFunctionId);
      hostConfig.setCp(cp);
      hostConfig.setUid(userId);
      hostConfig.setUkey(userKey);
      hostConfig.setDlg(dlg);
      hostConfig.setSimpleDlg(simpleDlg);
      hostConfig.setGeneral(general);
      hostConfig.setW(w);
      hostConfig.setH(h);
      hostConfig.setSrc(url);
      hostConfig.setProductCtx(productCtx);
      hostConfig.setTimeZone(timezone);

      String hostConfigJson = JsonUtils.toJson(hostConfig);

      velocityParams.put("hostConfigJson", hostConfigJson);
      velocityParams.put("ns", ns);
      velocityParams.put("plugin", PluginSetting.getPlugin());

    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
}
