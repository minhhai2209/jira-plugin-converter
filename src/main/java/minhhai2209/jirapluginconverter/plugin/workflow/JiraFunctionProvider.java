package minhhai2209.jirapluginconverter.plugin.workflow;

import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.wink.common.internal.UriBuilderImpl;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.rest.v2.issue.IncludedFields;
import com.atlassian.jira.rest.v2.issue.IssueBean;
import com.atlassian.jira.rest.v2.issue.builder.BeanBuilderFactory;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.atlassian.plugins.rest.common.json.DefaultJaxbJsonMarshaller;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.SimpleStep;
import com.opensymphony.workflow.spi.SimpleWorkflowEntry;

import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.setting.WorkflowPostFunctionUtils;
import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class JiraFunctionProvider extends AbstractJiraFunctionProvider {
  final private BeanBuilderFactory beanBuilderFactory;
  public JiraFunctionProvider(BeanBuilderFactory beanBuilderFactory){
    this.beanBuilderFactory = beanBuilderFactory;
  }
  @Override
  public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
    try {
      String fullModuleKey = ((String)args.get("full.module.key"));
      if(null == fullModuleKey){
        throw new Exception("full.module.key is not available");
      }

      String uri = WorkflowPostFunctionUtils.getWorkflowPostFuntion(fullModuleKey.replaceFirst(PluginSetting.PLUGIN_KEY, "")).getTriggered().getUrl();
      if (uri == null) {
        throw new Exception("URI is empty");
      }

      HttpPost post = new HttpPost(PluginSetting.getPluginBaseUrl() + uri);
      String json = this.getJSON(transientVars, args);
      post.setEntity(new StringEntity(json));
      post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

      String jwt = JwtComposer.compose(KeyUtils.getClientKey(), KeyUtils.getSharedSecret(), "POST", uri, null, null);
      if (jwt != null) {
        post.addHeader("Authorization", "JWT " + jwt);
      }

      HttpClient httpClient = HttpClientFactory.build();
      httpClient.execute(post);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private static String getUrl(String uri) {
    return uri == null ? null : PluginSetting.getPluginBaseUrl() + uri;
  }

  private String getJSON(Map transientVars, Map args) throws JSONException {
    final ApplicationUser currentUser = this.getCallerUser(transientVars, args);
    final Issue issue = this.getIssue(transientVars);

    IssueBean issueBean = beanBuilderFactory.newIssueBeanBuilder(issue, IncludedFields.includeAllByDefault(null))
            .uriBuilder(new UriBuilderImpl())
            .build();

    final DefaultJaxbJsonMarshaller jsonMarshaller = new DefaultJaxbJsonMarshaller();
    String text = jsonMarshaller.marshal(issueBean);
    JSONObject issueJson = new JSONObject(text);

    JSONObject configurationJson = new JSONObject();
    configurationJson.put("value", args.get("remoteWorkflowPostFunctionConfiguration"));

    JSONObject transitionJson = new JSONObject();
    transitionJson.put("to_status", ((ArrayList<SimpleStep>)transientVars.get("currentSteps")).get(0).getStatus());
    transitionJson.put("from_status", ((SimpleStep)transientVars.get("createdStep")).getStatus());
    transitionJson.put("transitionId", ((ArrayList<SimpleStep>)transientVars.get("currentSteps")).get(0).getActionId());
    transitionJson.put("transitionName", "");
    transitionJson.put("workflowId", ((SimpleWorkflowEntry)transientVars.get("entry")).getId());
    transitionJson.put("workflowName", ((SimpleWorkflowEntry)transientVars.get("entry")).getWorkflowName());

    JSONObject parentJson = new JSONObject();
    parentJson.put("issue", issueJson);
    parentJson.put("configuration", configurationJson);
    parentJson.put("transition", transitionJson);
    parentJson.put("user_key", currentUser.getKey());

    return parentJson.toString();
  }
}
