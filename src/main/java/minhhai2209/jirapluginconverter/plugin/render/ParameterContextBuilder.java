package minhhai2209.jirapluginconverter.plugin.render;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.browse.BrowseContext;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ParameterContextBuilder {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @SuppressWarnings("unchecked")
  private static void buildContextParams(HttpServletRequest request, Map<String, String> acContext) {

    try {

      acContext.put("issue.id", "");
      acContext.put("issue.key", "");
      acContext.put("issuetype.id", "");
      acContext.put("project.id", "");
      acContext.put("project.key", "");
      acContext.put("version.id", "");
      acContext.put("component.id", "");

      Map<String, String> productContext;

      Map<String, String[]> contextParams = request.getParameterMap();
      String[] productContexts = contextParams.get("product-context");
      if (productContexts != null && productContexts.length > 0) {
        String productContextAsString = productContexts[0];
        TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {};
        productContext = objectMapper.readValue(productContextAsString, typeReference);
        for (Map.Entry<String, String> param : productContext.entrySet()) {
          String value = param.getValue();
          if (value == null || value.contains("$")) {
            param.setValue("");
          }
        }
        acContext.putAll(productContext);
      } else {
        productContext = null;
      }

      ProjectManager projectManager = ComponentAccessor.getProjectManager();
      IssueManager issueManager = ComponentAccessor.getIssueManager();

      Project project = null;

      String issueKey;
      if (contextParams.containsKey("issueKey")) {
        issueKey = contextParams.get("issueKey")[0];
      } else if (productContext != null) {
        issueKey = productContext.get("issueKey");
      } else {
        issueKey = null;
      }
      if (issueKey != null && !issueKey.contains("$")) {
        acContext.put("issue.key", issueKey);
        MutableIssue issue = issueManager.getIssueObject(issueKey);
        if (issue != null) {
          project = issue.getProjectObject();
          acContext.put("issue.id", issue.getId().toString());
          acContext.put("issuetype.id", issue.getIssueTypeId());
          acContext.put("project.id", project.getId().toString());
          acContext.put("project.key", project.getKey());
        }
      }

      if (project == null) {
        String projectKey;
        if (contextParams.containsKey("projectKey")) {
          projectKey = contextParams.get("projectKey")[0];
        } else if (productContext != null) {
          projectKey = productContext.get("projectKey");
        } else {
          projectKey = null;
        }
        if (projectKey != null && !projectKey.contains("$")) {
          acContext.put("project.key", projectKey);
          project = projectManager.getProjectByCurrentKey(projectKey);
          if (project != null) {
            acContext.put("project.id", project.getId().toString());
          }
        }
      }

      if (contextParams.containsKey("versionId")) {
        String versionId = contextParams.get("versionId")[0];
        if (!versionId.contains("$")) {
          acContext.put("version.id", versionId);
        }
      }

      if (contextParams.containsKey("componentId")) {
        String componentId = contextParams.get("componentId")[0];
        if (!componentId.contains("$")) {
          acContext.put("component.id", componentId);
        }
      }
    } catch (Exception e) {
      // do nothing
      e.printStackTrace();
    }
  }

  private static void buildContextParams(Map<String, Object> contextParams, Map<String, String> acContext) {

    try {

      Object o = contextParams.get("issue");
      if (o != null) {
        if (o instanceof Issue) {
          Issue issue = (Issue) o;
          acContext.put("issue.key", issue.getKey());
          acContext.put("issue.id", issue.getId().toString());
          acContext.put("issuetype.id", issue.getIssueTypeId());
        }
      }


      o = contextParams.get("project");
      if (o != null) {
        if (o instanceof Project) {
          Project project = (Project) o;
          acContext.put("project.key", project.getKey());
          acContext.put("project.id", project.getId().toString());
        }
      }


      o = contextParams.get("postFunctionId");
      if (o != null) {
        acContext.put("postFunction.id", (String) o);
      }

      o = contextParams.get("postFunctionConfig");
      if (o != null) {
        acContext.put("postFunction.config", URLEncoder.encode((String) o, "UTF-8"));
      } else {
        acContext.put("postFunction.config", "");
      }
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private static void buildContextParams(Issue issue, Map<String, String> acContext) {

    if (issue != null) {
      acContext.put("issue.key", issue.getKey());
      acContext.put("issue.id", issue.getId().toString());
      acContext.put("issuetype.id", issue.getIssueTypeId());
    }
  }

  private static void buildContextParams(BrowseContext browseContext, Map<String, String> acContext) {

    Project project = browseContext.getProject();
    acContext.put("project.key", project.getKey());
    acContext.put("project.id", project.getId().toString());
  }

  public static String buildUrl(String url, Map<String, String> acContext) {
    StrSubstitutor substitutor = new StrSubstitutor(acContext, "${", "}");
    url = substitutor.replace(url);
    substitutor = new StrSubstitutor(acContext, "{", "}");
    url = substitutor.replace(url);
    return url;
  }

  public static Map<String, String> buildContext(
      HttpServletRequest request, Map<String, Object> contextParams, Issue issue, BrowseContext browseContext) {
    Map<String, String> acContext = new HashMap<String, String>();
    if (request != null) {
      buildContextParams(request, acContext);
    } else if (contextParams != null) {
      buildContextParams(contextParams, acContext);
    } else if (issue != null) {
      buildContextParams(issue, acContext);
    } else if (browseContext != null) {
      buildContextParams(browseContext, acContext);
    }
    return acContext;
  }

  public static Map<String, String> buildWorkflowContext( Map<String, Object> contextParams ) {
    try {
      Map<String, String> acContext = new HashMap<String, String>();

      Object o = contextParams.get("postFunctionId");
      if (o != null) {
        acContext.put("postFunction.id", (String) o);
      }

      o = contextParams.get("postFunctionConfig");
      if (o != null) {
        acContext.put("postFunction.config", URLEncoder.encode((String) o, "UTF-8"));
      } else {
        acContext.put("postFunction.config", "");
      }

      return acContext;
    } catch (UnsupportedEncodingException e) {
      ExceptionUtils.throwUnchecked(e);
    }

    return null;
  }
}
