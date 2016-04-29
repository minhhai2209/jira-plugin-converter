package minhhai2209.jirapluginconverter.plugin.render;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.browse.BrowseContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.message.LocaleResolver;
import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;
import minhhai2209.jirapluginconverter.plugin.iframe.HostConfig;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.*;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import org.apache.http.client.utils.URIBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ProjectTabPanelRenderer extends AbstractProjectTabPanel {

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public ProjectTabPanelRenderer(TimeZoneService timeZoneService, LocaleResolver localeResolver) {
    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
  }

  @Override
  public boolean showPanel(BrowseContext browseContext) {
    return false;
  }

  @Override
  public Map<String, Object> createVelocityParams(BrowseContext browseContext) {
    try {

      String moduleKey = descriptor.getKey();

      TabPanel tabPanel = ProjectTabPanelUtils.getProjectTabPanel(moduleKey);

      String fullUrl = ProjectTabPanelUtils.getFullUrl(tabPanel);

      String title = tabPanel.getName().getValue();

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      Map<String, String> productContext = ParameterContextBuilder.buildContext(null, null, null, browseContext);

      String xdm_e = JiraUtils.getBaseUrl();
      String cp = JiraUtils.getContextPath();
      String ns = PluginSetting.getDescriptor().getKey() + "__" + moduleKey;
      String xdm_c = "channel-" + ns;
      String simpleDlg = "";
      String dlg = "";
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
            tabPanel.getUrl());
        uriBuilder.addParameter("jwt", jwt);
      }
      String url = uriBuilder.toString();

      HostConfig hostConfig = new HostConfig();
      hostConfig.setNs(ns);
      hostConfig.setKey(PluginSetting.getDescriptor().getKey());
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
      context.put("title", title);
      context.put("plugin", PluginSetting.getPlugin());

      context.put("projectKey", browseContext.getProject().getKey());
      context.put("adminActiveTab", moduleKey);

      return context;

    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }

    return null;
  }
}
