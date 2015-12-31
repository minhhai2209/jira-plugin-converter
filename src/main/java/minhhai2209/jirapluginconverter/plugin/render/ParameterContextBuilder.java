package minhhai2209.jirapluginconverter.plugin.render;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.text.StrSubstitutor;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;

public class ParameterContextBuilder {

  private Map<String, String> contextMapper;

  public ParameterContextBuilder() {
    buildContextMapper();
  }

  private void buildContextMapper() {
    contextMapper = new HashMap<String, String>();
    contextMapper.put("issue.id", "");
    contextMapper.put("issue.key", "");
    contextMapper.put("issuetype.id", "");
    contextMapper.put("project.id", "");
    contextMapper.put("project.key", "");
    contextMapper.put("version.id", "");
    contextMapper.put("component.id", "");
  }

  @SuppressWarnings("unchecked")
  public void buildContextParams(HttpServletRequest request, Map<String, String> context) {
    Map<String, String[]> contextParams = request.getParameterMap();
    ProjectManager projectManager = ComponentAccessor.getProjectManager();
    IssueManager issueManager = ComponentAccessor.getIssueManager();

    if (contextParams.containsKey("issueKey")) {
      String issueKey = contextParams.get("issueKey")[0];
      context.putAll(contextMapper);
      if (!issueKey.contains("$")) {
        context.put("issue.key", issueKey);
        MutableIssue issue = issueManager.getIssueByCurrentKey(issueKey);
        if (issue != null) {
          context.put("issue.id", issue.getId().toString());
          context.put("issuetype.id", issue.getIssueTypeId());
        }
      }
    }

    if (contextParams.containsKey("projectKey")) {
      String projectKey = contextParams.get("projectKey")[0];
      if (!projectKey.contains("$")) {
        context.put("project.key", projectKey);
        Project project = projectManager.getProjectByCurrentKey(projectKey);
        if (project != null) {
          context.put("project.id", project.getId().toString());
        }
      }
    }

    if (contextParams.containsKey("versionId")) {
      String versionId = contextParams.get("versionId")[0];
      if (!versionId.contains("$")) {
        context.put("version.id", versionId);
      }
    }

    if (contextParams.containsKey("componentId")) {
      String componentId = contextParams.get("componentId")[0];
      if (!componentId.contains("$")) {
        context.put("component.id", componentId);
      }
    }
  }

  public String buildUrl(HttpServletRequest request, String url) {
    Map<String, String> contextParams = new HashMap<String, String>();
    buildContextParams(request, contextParams);
    StrSubstitutor substitutor = new StrSubstitutor(contextParams, "${", "}");
    url = substitutor.replace(url);
    return url;
  }
}
