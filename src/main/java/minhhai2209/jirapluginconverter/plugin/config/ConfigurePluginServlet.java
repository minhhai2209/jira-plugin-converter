package minhhai2209.jirapluginconverter.plugin.config;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.plugin.iframe.HostConfig;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.lifecycle.PluginLifeCycleEventHandler;
import minhhai2209.jirapluginconverter.plugin.render.ParameterContextBuilder;
import minhhai2209.jirapluginconverter.plugin.setting.*;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ConfigurePluginServlet extends HttpServlet {

  private static final long serialVersionUID = 5861197118433423444L;

  private UserManager userManager;

  private LoginUriProvider loginUriProvider;

  private TemplateRenderer renderer;

  private PluginSettingsFactory pluginSettingsFactory;

  private TransactionTemplate transactionTemplate;

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  private PluginLifeCycleEventHandler pluginLifeCycleEventHandler;

  private static final String RESPONSE_CONTENT_TYPE = "text/html;charset=utf-8";

  public static String DB_URL;
  public static String DB_USER;

  private static final String UI_URL = "url";
  private static final String UI_USER = "user";
  private static final String UI_USERS = "users";

  // Injected by JIRA
  public ConfigurePluginServlet(
      UserManager userManager,
      LoginUriProvider loginUriProvider,
      TemplateRenderer renderer,
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate,
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver,
      PluginLifeCycleEventHandler pluginLifeCycleEventHandler) {

    this.userManager = userManager;
    this.loginUriProvider = loginUriProvider;
    this.renderer = renderer;
    this.pluginSettingsFactory = pluginSettingsFactory;
    this.transactionTemplate = transactionTemplate;
    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
    this.pluginLifeCycleEventHandler = pluginLifeCycleEventHandler;

    DB_URL = PluginSetting.getDescriptor().getKey() + ".url";
    DB_USER = PluginSetting.getDescriptor().getKey() + ".user";
  }

  /**
   * Config screen
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doGet(final HttpServletRequest request, final HttpServletResponse response)
      throws IOException, ServletException {

    runIfAdmin(request, response, new Runnable() {

      @Override
      public void run() {
        final Map<String, Object> context = new HashMap<String, Object>();
        addListUsersToContext(context);
        transactionTemplate.execute(new TransactionCallback() {
          @Override
          public Object doInTransaction() {

            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();

            String url = (String) settings.get(DB_URL);
            String user = (String) settings.get(DB_USER);

            updateContext(context, UI_URL, url);
            updateContext(context, UI_USER, user);
            return null;
          }

        });
        addConfigurePage(request, context);
        render(response, context);
      }
    });
  }

  /**
   * Save config
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void doPost(final HttpServletRequest request, final HttpServletResponse response)
      throws IOException, ServletException {

    runIfAdmin(request, response, new Runnable() {

      @Override
      public void run() {
        Map<String, Object> context = new HashMap<String, Object>();

        final StringBuilder error = new StringBuilder("");

        try {
          final String url = request.getParameter(UI_URL);
          final String user = request.getParameter(UI_USER);

          updateContext(context, UI_URL, url);
          updateContext(context, UI_USER, user);
          addListUsersToContext(context);

          // login to qTest
          boolean urlChanged = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction() {
              try {
                PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                String oldUrl = (String) settings.get(DB_URL);
                settings.put(DB_URL, url);
                settings.put(DB_USER, user);
                return !StringUtils.equals(oldUrl, url);
              } catch (Exception e) {
                error.append(ExceptionUtils.getStackTrace(e));
              }
              return null;
            }
          });

          if (urlChanged) {
            pluginLifeCycleEventHandler.onInstalled(error);
          }

        } catch (Exception e) {
          error.append(ExceptionUtils.getStackTrace(e));
        }

        if (error.length() > 0) {
          updateContext(context, "error", error.toString());
        }
        addConfigurePage(request, context);
        render(response, context);
      }

    });
  }

  // Copied from tutorial
  private void redirectToLogin(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  private URI getUri(HttpServletRequest request) {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null) {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }

  /**
   * Put to map, null is converted to empty string
   * @param context
   * @param key
   * @param value
   */
  private void updateContext(Map<String, Object> context, String key, Object value) {
    context.put(key, value == null ? "" : value);
  }

  private void render(HttpServletResponse response, Map<String, Object> context) {
    try {
      response.setContentType(RESPONSE_CONTENT_TYPE);
      renderer.render("templates/configure-plugin.vm", context, response.getWriter());
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  private void addListUsersToContext(final Map<String, Object> context) {
    UserUtil userUtil = ComponentAccessor.getUserUtil();
    Collection<ApplicationUser> applicationUsers = userUtil.getAllApplicationUsers();
    Map<String, String> users = new HashMap<String, String>();
    for (ApplicationUser applicationUser : applicationUsers) {
      if (applicationUser.isActive()) {
        String userKey = applicationUser.getKey();
        String userName = userUtil.getDisplayableNameSafely(applicationUser);
        users.put(userKey, userName);
      }
    }
    updateContext(context, UI_USERS, users);
  }

  private void runIfAdmin(HttpServletRequest request, HttpServletResponse response, Runnable task) throws IOException {
    // Require admin account
    UserProfile userProfile = userManager.getRemoteUser(request);
    if (userProfile == null) {
      redirectToLogin(request, response);
    } else {
      UserKey userKey = userProfile.getUserKey();
      if (userManager.isSystemAdmin(userKey)) {
        task.run();
      } else {
        redirectToLogin(request, response);
      }
    }
  }

  private void addConfigurePage(HttpServletRequest request, Map<String, Object> context) {

    try {

      Modules modules = PluginSetting.getModules();
      if (modules != null) {
        Page page = modules.getConfigurePage();

        if (page != null) {

          String moduleKey = page.getKey();

          String fullUrl = PageUtils.getFullUrl(page);

          String title = page.getName().getValue();

          JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
          ApplicationUser user = authenticationContext != null ? authenticationContext.getUser() : null;
          JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
          TimeZone timeZone = user == null ?
              timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
              timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

          String location = page.getLocation();
          boolean chrome = !("none".equals(location) || "no-location".equals(location));

          Map<String, String> productContext = ParameterContextBuilder.buildContext(request, null, null);

          String dlg;
          String general;
          String w;
          String h;

          String xdm_e = JiraUtils.getBaseUrl();
          String cp = JiraUtils.getContextPath();
          String ns = PluginSetting.getDescriptor().getKey() + "__" + moduleKey;
          String xdm_c = "channel-" + ns;
          String simpleDlg = "";
          if (chrome) {
            dlg = "";
            general = "1";
            w = "";
            h = "";
          } else {
            dlg = "1";
            general = "";
            w = "100%";
            h = "100%";
          }
          String productCtx = JsonUtils.toJson(productContext);
          String timezone = timeZone.getID();
          String loc = LocaleUtils.getLocale(localeResolver);
          String userId = user != null ? user.getUsername() : "";
          String userKey = user != null ? user.getKey() : "";
          String lic = LicenseUtils.getLic();
          String cv = "";
          String uiParams = request.getParameter("ui-params");

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

          if (uiParams != null) {
            uriBuilder.addParameter("ui-params", uiParams);
          }

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

          context.put("hostConfigJson", hostConfigJson);
          context.put("ns", ns);
          context.put("title", title);
          context.put("chrome", chrome);
          context.put("plugin", PluginSetting.getPlugin());
        }
      }
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
}