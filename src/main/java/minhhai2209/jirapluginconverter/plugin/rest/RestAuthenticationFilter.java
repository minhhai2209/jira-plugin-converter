package minhhai2209.jirapluginconverter.plugin.rest;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtClaim;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtVerifier;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class RestAuthenticationFilter implements Filter {

  private static final String JWT_REALM = "JWT ";

  private final PluginSettingsFactory pluginSettingsFactory;
  private final TransactionTemplate transactionTemplate;

  public RestAuthenticationFilter(
      PluginSettingsFactory pluginSettingsFactory,
      TransactionTemplate transactionTemplate) {

    this.pluginSettingsFactory = pluginSettingsFactory;
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @SuppressWarnings("unchecked")
  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {

    boolean authorized = true;

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String authorization = request.getHeader("Authorization");
    String jwtString = null;

    // Look for jwt in headers
    if (authorization != null && authorization.startsWith(JWT_REALM)) {
      // only process if Authorization header is JWT token
      jwtString = authorization.substring(JWT_REALM.length());
    }

    // Look for jwt in query string
    if (jwtString == null) {
      jwtString = request.getParameter("jwt");
    }

    if (jwtString != null && jwtString.length() > 0) {
      String[] jwtSegements = jwtString.split("\\.");
      if (jwtSegements.length == 3) {
        String claimSegmentJson = new String(Base64.decodeBase64(jwtSegements[1]));
        JwtClaim unverifiedClaim = JsonUtils.fromJson(claimSegmentJson, JwtClaim.class);
        String descriptorKey = PluginSetting.getDescriptor().getKey();
        if (descriptorKey.equals(unverifiedClaim.getIss())) {
          // only process requests for this plugin
          String relativeURI = request.getRequestURI().substring(request.getContextPath().length());
          Map<String, String[]> parameterMap = request.getParameterMap();
          String method = request.getMethod();
          boolean verified = JwtVerifier.verify(
            relativeURI,
            jwtString,
            parameterMap,
            descriptorKey,
            KeyUtils.getSharedSecret(),
            method);
          if (verified) {
            ApplicationUser user = PluginSetting.getPluginUser();
            if (user != null) {
              HttpSession httpSession = request.getSession();
              httpSession.setAttribute(DefaultAuthenticator.LOGGED_IN_KEY, user);
              httpSession.setAttribute(DefaultAuthenticator.LOGGED_OUT_KEY, null);
            }
          } else {
            // only in this case does this filter block the request
            authorized = false;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
          }
        }
      }
    }
    if (authorized) {
      chain.doFilter(servletRequest, servletResponse);
    }
  }

  @Override
  public void destroy() {
  }
}
