package minhhai2209.jirapluginconverter.plugin.condition;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.fasterxml.jackson.databind.ObjectMapper;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.render.ParameterContextBuilder;
import minhhai2209.jirapluginconverter.plugin.setting.AuthenticationUtils;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.LicenseUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.util.Map;

public class RemoteCondition implements Condition {
  
  private Map<String, String> params;
  private String conditionUrl;
  private ObjectMapper om = new ObjectMapper();

  @Override
  public void init(Map<String, String> params) throws PluginParseException {
    this.params = params;
    this.conditionUrl = params.get("condition");
  }

  @Override
  public boolean shouldDisplay(Map<String, Object> context) {
    return conditionUrl == null || getRemoteCondition(context);
  }
  
  private boolean getRemoteCondition(Map<String, Object> context) {
    try {
      String baseUrl = PluginSetting.getPluginBaseUrl();
      String fullUrl = baseUrl + conditionUrl;

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getUser() : null;

      Map<String, String> productContext = ParameterContextBuilder.buildContext(null, context, null);
      String userKey = user != null ? user.getKey() : "";
      String lic = LicenseUtils.getLic();

      String urlWithContext = ParameterContextBuilder.buildUrl(fullUrl, productContext);

      URIBuilder builder = new URIBuilder(urlWithContext);
      if (params != null) {
        for (String key : params.keySet()) {
          if (!key.equals("condition")) {
            builder.addParameter(key, params.get(key));
          }
        }
      }
      builder.addParameter("lic", LicenseUtils.getLic())
          .addParameter("user_key", userKey);

      if (AuthenticationUtils.needsAuthentication()) {

        String jwt = JwtComposer.compose(
            KeyUtils.getClientKey(),
            KeyUtils.getSharedSecret(),
            "GET",
            builder,
            userKey,
            conditionUrl);
        builder.addParameter("jwt", jwt);
      }

      String url = builder.toString();

      HttpClient client = HttpClientFactory.build();
      HttpGet httpGet = new HttpGet(url);
      HttpResponse response = client.execute(httpGet);
      DisplayDto displayDto = om.readValue(response.getEntity().getContent(), DisplayDto.class);
      return displayDto.isShouldDisplay();
    } catch (Exception e) {
      //any exception will return false
    }
    
    return false;
  }

}
