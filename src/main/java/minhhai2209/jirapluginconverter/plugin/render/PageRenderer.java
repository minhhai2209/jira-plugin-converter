package minhhai2209.jirapluginconverter.plugin.render;

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
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.templaterenderer.TemplateRenderer;

import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.plugin.iframe.HostConfig;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.AuthenticationUtils;
import minhhai2209.jirapluginconverter.plugin.setting.JiraUtils;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PageUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.plugin.utils.RequestUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class PageRenderer extends HttpServlet {

  private static final long serialVersionUID = 6917800660560978125L;

  private static final String RESPONSE_CONTENT_TYPE = "text/html;charset=utf-8";

  private TemplateRenderer renderer;

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public PageRenderer(
      TemplateRenderer renderer,
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver) {

    this.renderer = renderer;
    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
  }

  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {

      PageType pageType;

      String moduleKey = RequestUtils.getModuleKey(request);
      Page page = PageUtils.getGeneralPage(moduleKey);
      if (page == null) {
        page = PageUtils.getAdminPage(moduleKey);
        if (page == null) {
          page = PageUtils.getConfigurePage(moduleKey);
          pageType = PageType.CONFIGURE;
        } else {
          pageType = PageType.ADMIN;
        }
      } else {
        pageType = PageType.GENERAL;
      }
      String fullUrl = PageUtils.getFullUrl(page);

      String title = page.getName().getValue();

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getLoggedInUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      Map<String, String> productContext = ParameterContextBuilder.buildContext(request, null, null);

      String xdm_e = JiraUtils.getBaseUrl();
      String cp = JiraUtils.getContextPath();
      String ns = PluginSetting.URL_SAFE_PLUGIN_KEY + "__" + moduleKey;
      String xdm_c = "channel-" + ns;
      String dlg = "";
      String simpleDlg = "";
      String general = "1";
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
            page.getUrl());
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

      String template;
      switch (pageType) {
        case GENERAL:
          template = "general-page";
          break;
        case ADMIN:
          template = "admin-page";
          break;
        case CONFIGURE:
          template = "configure-page";
          break;
        default:
          throw new IllegalStateException();
      }

      String location = page.getLocation();
      boolean chrome = !(location == null || location.equals("none") || location.equals("no-location"));

      Map<String, Object> context = new HashMap<String, Object>();
      context.put("hostConfigJson", hostConfigJson);
      context.put("ns", ns);
      context.put("title", title);
      context.put("chrome", chrome);
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
