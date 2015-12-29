package minhhai2209.jirapluginconverter.plugin.rest;

import java.io.IOException;
import java.util.Collection;

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
import minhhai2209.jirapluginconverter.plugin.setting.JiraUtils;
import minhhai2209.jirapluginconverter.plugin.setting.KeyUtils;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;

public class RestAuthenticationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {

    boolean authorized = true;

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String authorization = request.getHeader("Authorization");
    if (authorization != null) {
      if (authorization.startsWith("JWT")) {
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
          url += queryString;
        }
        String method = request.getMethod();
        JwtClaim claim = JwtVerifier.read(
            url,
            authorization,
            JiraUtils.getJiraBaseUrl(),
            PluginSetting.getDescriptor().getKey(),
            KeyUtils.getSharedSecret(),
            method);
        if (claim != null) {
          authorized = false;
          HttpServletResponse response = (HttpServletResponse) servletResponse;
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {

          UserUtil userUtil = ComponentAccessor.getUserUtil();
          Collection<User> admins = userUtil.getJiraAdministrators();
          User admin = Iterables.get(admins, 0);
          String adminName = admin.getName();
          ApplicationUser applicationAdmin = userUtil.getUserByName(adminName);
          HttpSession httpSession = request.getSession();
          httpSession.setAttribute(DefaultAuthenticator.LOGGED_IN_KEY, applicationAdmin);
          httpSession.setAttribute(DefaultAuthenticator.LOGGED_OUT_KEY, null);
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