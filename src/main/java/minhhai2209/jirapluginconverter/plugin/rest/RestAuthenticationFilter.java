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
import javax.servlet.http.HttpSession;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.google.common.collect.Iterables;

public class RestAuthenticationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String authorization = request.getHeader("Authorization");
    if (authorization != null) {
      if (authorization.startsWith("JWT")) {
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
    chain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {
  }
}