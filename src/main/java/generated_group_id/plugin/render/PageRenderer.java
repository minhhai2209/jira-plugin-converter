package generated_group_id.plugin.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.templaterenderer.TemplateRenderer;

import generated_group_id.plugin.iframe.HostConfig;
import generated_group_id.plugin.setting.PluginSetting;
import generated_group_id.plugin.staticcontent.StaticResourcesFilter;
import generated_group_id.utils.LocaleUtils;
import generated_group_id.utils.http.RequestUtils;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class PageRenderer extends HttpServlet {

  private static final long serialVersionUID = 6917800660560978125L;

  private static final String RESPONSE_CONTENT_TYPE = "text/html;charset=utf-8";

  private TemplateRenderer renderer;

  private ApplicationProperties applicationProperties;

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public PageRenderer(
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
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {

      boolean isAdmin = false;

      String moduleKey = RequestUtils.getModuleKey(request);
      Page page = PluginSetting.getGeneralPage(moduleKey);
      if (page == null) {
        isAdmin = true;
        page = PluginSetting.getAdminPage(moduleKey);
      }
      String url = PluginSetting.getUrl(page);

      String title = page.getName().getValue();

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      String xdm_e = applicationProperties.getBaseUrl(UrlMode.ABSOLUTE);
      String cp = applicationProperties.getBaseUrl(UrlMode.RELATIVE);
      String pp = StaticResourcesFilter.HOST_RESOURCE_PATH;
      String ns = PluginSetting.URL_SAFE_PLUGIN_KEY + "__" + moduleKey;
      String xdm_c = "channel-" + ns;
      String dlg = "";
      String simpleDlg = "";
      String general = "1";
      String w = "";
      String h = "";
      String productCtx = "{}";
      String timezone = timeZone.getID();
      String loc = LocaleUtils.getLocale(localeResolver);
      String userId = user != null ? user.getUsername() : "";
      String userKey = user != null ? user.getKey() : "";
      String lic = "none";
      String cv = "";

      ParameterContextBuilder paramContextBuilder = new ParameterContextBuilder();
      url = paramContextBuilder.buildUrl(request, url);
      
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

      String template = "general-page";
      if (isAdmin) {
        template = "admin-page";
      }

      Map<String, Object> context = new HashMap<String, Object>();
      context.put("hostConfigJson", hostConfigJson);
      context.put("ns", ns);
      context.put("title", title);
      render(template, response, context);

    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private void render(String vm, HttpServletResponse response, Map<String, Object> context) throws IOException {
    response.setContentType(RESPONSE_CONTENT_TYPE);
    renderer.render("templates/" + vm + ".vm", context, response.getWriter());
  }
}
