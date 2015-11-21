package generated_group_id.plugin.rest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.google.common.collect.Iterables;

import generated_group_id.utils.http.RequestUtils;

public class RestProxy extends HttpServlet {

  private static final long serialVersionUID = 6339534697095776216L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    forward(request, response);
  }

  private void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    UserUtil userUtil = ComponentAccessor.getUserUtil();
    Collection<User> admins = userUtil.getJiraAdministrators();
    User admin = Iterables.get(admins, 0);
    String adminName = admin.getName();
    ApplicationUser applicationAdmin = userUtil.getUserByName(adminName);
    ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(applicationAdmin);

    String moduleKey = RequestUtils.getModuleKey(request);
    request.getRequestDispatcher("/" + moduleKey).forward(request, response);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    forward(req, resp);
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    forward(req, resp);
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    forward(req, resp);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    forward(req, resp);
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    forward(req, resp);
  }

}
