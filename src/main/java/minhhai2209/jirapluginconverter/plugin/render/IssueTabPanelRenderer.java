package minhhai2209.jirapluginconverter.plugin.render;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.client.utils.URIBuilder;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.velocity.VelocityManager;

import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;
import minhhai2209.jirapluginconverter.plugin.iframe.HostConfig;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.AuthenticationUtils;
import minhhai2209.jirapluginconverter.plugin.setting.JiraUtils;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.setting.TabPanelUtils;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class IssueTabPanelRenderer extends AbstractIssueTabPanel {

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public IssueTabPanelRenderer(
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver) {

    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public List getActions(Issue issue, User remoteUser) {

    try {

      String moduleKey = descriptor.getKey();
      TabPanel tabPanel = TabPanelUtils.getJiraIssueTabPanel(moduleKey);
      String fullUrl = TabPanelUtils.getFullUrl(tabPanel);

      UserUtil userUtil = ComponentAccessor.getUserUtil();
      String userName = remoteUser.getName();
      ApplicationUser user = userUtil.getUserByName(userName);
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      Map<String, String> productContext = ParameterContextBuilder.buildContext(null, null, issue);

      String xdm_e = JiraUtils.getBaseUrl();
      String cp = JiraUtils.getContextPath();
      String ns = PluginSetting.URL_SAFE_PLUGIN_KEY + "__" + moduleKey;
      String xdm_c = "channel-" + ns;
      String dlg = "";
      String simpleDlg = "";
      String general = "";
      String w = "";
      String h = "";
      String productCtx = JsonUtils.toJson(productContext);
      String timezone = timeZone.getID();
      String loc = LocaleUtils.getLocale(localeResolver);
      String userId = user != null ? user.getUsername() : "";
      String userKey = user != null ? user.getKey() : "";
      String lic = "none";
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
            tabPanel.getUrl());
        uriBuilder.addParameter("jwt", jwt);
      }
      String url = uriBuilder.toString();

      HostConfig hostConfig = new HostConfig();
      hostConfig.setNs(ns);
      hostConfig.setKey(PluginSetting.URL_SAFE_PLUGIN_KEY);
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

      Map<String, Object> context = new HashMap<String, Object>();
      context.put("hostConfigJson", hostConfigJson);
      context.put("ns", ns);
      String html = render("issue-tab-panel", context);

      return Collections.singletonList(new GenericMessageAction(html));

    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private String render(String template, Map<String, Object> context) {
    ApplicationProperties ap = ComponentAccessor.getApplicationProperties();
    String webworkEncoding = ap.getString(APKeys.JIRA_WEBWORK_ENCODING);
    VelocityManager vm = ComponentAccessor.getVelocityManager();
    VelocityParamFactory vp = ComponentAccessor.getVelocityParamFactory();
    Map vc = vp.getDefaultVelocityParams();
    vc.putAll(context);
    String html = vm.getEncodedBody("templates/", template + ".vm", webworkEncoding, vc);
    return html;
  }

  @Override
  public boolean showPanel(Issue issue, User remoteUser) {
    return true;
  }

}
