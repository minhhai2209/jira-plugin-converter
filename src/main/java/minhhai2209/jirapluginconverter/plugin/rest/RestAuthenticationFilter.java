package minhhai2209.jirapluginconverter.plugin.rest;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.google.common.collect.Iterables;

import minhhai2209.jirapluginconverter.plugin.jwt.JwtClaim;
import minhhai2209.jirapluginconverter.plugin.jwt.JwtVerifier;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.utils.JsonUtils;

public class RestAuthenticationFilter implements Filter {
  private static final String JWT_REALM = "JWT ";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {

    boolean authorized = true;

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.startsWith(JWT_REALM)) {
      // only process if Authorization header is JWT token
      String jwtString = authorization.substring(JWT_REALM.length());
      String[] jwtSegements = jwtString.split("\\.");
      if (jwtSegements.length == 3) {
        String claimSegmentJson = new String(Base64.getDecoder().decode(jwtSegements[1]));
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
            UserUtil userUtil = ComponentAccessor.getUserUtil();
            Collection<User> admins = userUtil.getJiraAdministrators();
            User admin = Iterables.get(admins, 0);
            String adminName = admin.getName();
            ApplicationUser applicationAdmin = userUtil.getUserByName(adminName);
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute(DefaultAuthenticator.LOGGED_IN_KEY, applicationAdmin);
            httpSession.setAttribute(DefaultAuthenticator.LOGGED_OUT_KEY, null);
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
