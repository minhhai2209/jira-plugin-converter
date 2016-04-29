package minhhai2209.jirapluginconverter.plugin.render;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.timezone.TimeZoneService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.message.LocaleResolver;
import minhhai2209.jirapluginconverter.connect.descriptor.Context;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItemTarget.Type;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtComposer;
import minhhai2209.jirapluginconverter.plugin.setting.*;
import minhhai2209.jirapluginconverter.plugin.utils.EnumUtils;
import minhhai2209.jirapluginconverter.plugin.utils.LocaleUtils;
import minhhai2209.jirapluginconverter.plugin.utils.RequestUtils;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;

public class WebItemRenderer extends HttpServlet {

  private static final long serialVersionUID = 6917800660560978125L;

  private TimeZoneService timeZoneService;

  private LocaleResolver localeResolver;

  public WebItemRenderer(
      TimeZoneService timeZoneService,
      LocaleResolver localeResolver) {

    this.timeZoneService = timeZoneService;
    this.localeResolver = localeResolver;
  }

  @Override
  public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {

      String moduleKey = RequestUtils.getModuleKey(request);
      WebItem webItem = WebItemUtils.getWebItem(moduleKey);
      String webItemUrl = webItem.getUrl();
      String fullUrl = WebItemUtils.getFullUrl(webItem);
      WebItemTarget target = webItem.getTarget();
      Type type = null;
      if (target != null) {
        type = target.getType();
      }
      if (type == null) {
        type = Type.page;
      }
      Context context = webItem.getContext();
      if (context == null) {
        context = Context.addon;
      }

      JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
      ApplicationUser user = authenticationContext != null ? authenticationContext.getLoggedInUser() : null;
      JiraServiceContextImpl jiraServiceContext = new JiraServiceContextImpl(user);
      TimeZone timeZone = user == null ?
          timeZoneService.getDefaultTimeZoneInfo(jiraServiceContext).toTimeZone() :
          timeZoneService.getUserTimeZoneInfo(jiraServiceContext).toTimeZone();

      Map<String, String> productContext = ParameterContextBuilder.buildContext(request, null, null, null);

      String xdm_e = JiraUtils.getBaseUrl();
      String cp = JiraUtils.getContextPath();
      String ns = PluginSetting.getDescriptor().getKey() + "__" + moduleKey;
      String xdm_c = "channel-" + ns;
      String timezone = timeZone.getID();
      String loc = LocaleUtils.getLocale(localeResolver);
      String userId = user != null ? user.getUsername() : "";
      String userKey = user != null ? user.getKey() : "";
      String lic = LicenseUtils.getLic();
      String cv = "";

      String urlWithContext = ParameterContextBuilder.buildUrl(fullUrl, productContext);

      URIBuilder uriBuilder = new URIBuilder(urlWithContext);
      if (EnumUtils.equals(type, Type.dialog) ||
          (EnumUtils.equals(type, Type.page)
          && EnumUtils.equals(context, Context.addon)
          && !(webItemUrl.startsWith("http://") || webItemUrl.startsWith("https://")))) {
        uriBuilder = uriBuilder.addParameter("tz", timezone)
            .addParameter("loc", loc)
            .addParameter("user_id", userId)
            .addParameter("user_key", userKey)
            .addParameter("xdm_e", xdm_e)
            .addParameter("xdm_c", xdm_c)
            .addParameter("cp", cp)
            .addParameter("lic", lic)
            .addParameter("cv", cv);
      }

      if (AuthenticationUtils.needsAuthentication()) {
        String jwt = JwtComposer.compose(
            KeyUtils.getClientKey(),
            KeyUtils.getSharedSecret(),
            "GET",
            uriBuilder,
            userKey,
            webItemUrl);
        uriBuilder.addParameter("jwt", jwt);
      }
      String url = uriBuilder.toString();

      if (EnumUtils.equals(type, Type.page)) {

        response.sendRedirect(url);

      }

    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
}
