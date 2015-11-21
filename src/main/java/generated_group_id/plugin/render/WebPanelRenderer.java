package generated_group_id.plugin.render;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.client.utils.URIBuilder;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.templaterenderer.TemplateRenderer;

import generated_group_id.plugin.iframe.HostConfig;
import generated_group_id.plugin.setting.PluginSetting;
import generated_group_id.plugin.staticcontent.StaticResourcesFilter;
import generated_group_id.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class WebPanelRenderer implements com.atlassian.plugin.web.renderer.WebPanelRenderer {

  private TemplateRenderer renderer;

  private ApplicationProperties applicationProperties;

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public WebPanelRenderer(
      TemplateRenderer renderer,
      ApplicationProperties applicationProperties,
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver) {

    this.renderer = renderer;
    this.applicationProperties = applicationProperties;
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

      WebPanel webPanel = PluginSetting.getWebPanel(templateName);
      String url = PluginSetting.getUrl(webPanel);

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      String xdm_e = applicationProperties.getBaseUrl(UrlMode.ABSOLUTE);
      String cp = applicationProperties.getBaseUrl(UrlMode.RELATIVE);
      String pp = StaticResourcesFilter.HOST_RESOURCE_PATH;
      String ns = PluginSetting.URL_SAFE_PLUGIN_KEY + "__" + templateName;
      String xdm_c = "channel-" + ns;
      String dlg = "";
      String simpleDlg = dlg;
      String general = "";
      String w = "";
      String h = "";
      String productCtx = "";
      String timezone = timeZone.getID();
      String loc = LocaleUtils.getLocale(localeResolver);
      String userId = user != null ? user.getUsername() : "";
      String userKey = user != null ? user.getKey() : "";
      String lic = "none";
      String cv = "";

      url = new URIBuilder(url)
          .addParameter("tz", timezone)
          .addParameter("loc", loc)
          .addParameter("user_id", userId)
          .addParameter("user_key", userKey)
          .addParameter("xdm_e", xdm_e)
          .addParameter("xdm_c", xdm_c)
          .addParameter("cp", cp)
          .addParameter("pp", pp)
          .addParameter("lic", lic)
          .addParameter("cv", cv)
          .toString();

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

      Map<String, Object> viewContext = new HashMap<String, Object>();
      viewContext.put("hostConfigJson", hostConfigJson);
      viewContext.put("ns", ns);
      render("web-item", writer, viewContext);

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
