package minhhai2209.jirapluginconverter.plugin.render;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.plugin.iframe.HostConfig;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.*;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class WebPanelRenderer implements com.atlassian.plugin.web.renderer.WebPanelRenderer {

  private TemplateRenderer renderer;

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public WebPanelRenderer(
      TemplateRenderer renderer,
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver) {

    this.renderer = renderer;
    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
  }

  @Override
  public String getResourceType() {
    return PluginSetting.PLUGIN_KEY + "-iframe";
  }

  @Override
  public void render(String templateName, Plugin plugin, Map<String, Object> context, Writer writer)
      throws RendererException, IOException {

    try {

      String moduleKey = templateName;

      WebPanel webPanel = WebPanelUtils.getWebPanel(moduleKey);
      String fullUrl = WebPanelUtils.getFullUrl(webPanel);

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getLoggedInUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      Map<String, String> productContext = ParameterContextBuilder.buildContext(null, context, null, null);

      String xdm_e = JiraUtils.getBaseUrl();
      String cp = JiraUtils.getContextPath();
      String ns = PluginSetting.getDescriptor().getKey() + "__" + moduleKey;
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
            webPanel.getUrl());
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

      Map<String, Object> viewContext = new HashMap<String, Object>();
      viewContext.put("hostConfigJson", hostConfigJson);
      viewContext.put("ns", ns);
      viewContext.put("plugin", PluginSetting.getPlugin());
      render("web-panel", writer, viewContext);

    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private void render(String vm, Writer writer, Map<String, Object> context) throws IOException {
    renderer.render("templates/" + vm + ".vm", context, writer);
  }

  @Override
  public String renderFragment(String fragment, Plugin plugin, Map<String, Object> context) throws RendererException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void renderFragment(Writer writer, String fragment, Plugin plugin, Map<String, Object> context)
      throws RendererException, IOException {
    throw new UnsupportedOperationException();
  }

}
