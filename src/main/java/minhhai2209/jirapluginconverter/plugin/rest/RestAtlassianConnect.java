package minhhai2209.jirapluginconverter.plugin.rest;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserKey;

@Path("/")
public class RestAtlassianConnect
{
  private UserManager userManager;

  public RestAtlassianConnect(UserManager userManager){
    this.userManager = userManager;
  }

  @GET
  @Path("addons/{addon}/properties/{property}")
  @Produces({ "application/json" })
  public Response getProperty(@PathParam("addon") String addon, @PathParam("property") String property, @Context HttpServletRequest request) {
    if (isAdmin(request)) {
      String url = request.getRequestURL().toString();
      AddonProperties respValue = new AddonProperties(Properties.getPerProject(property,"perProject"),Properties.getEnabledProjects(property,"enabledProjects"));
      return Response.ok(new PropertiesResponse(property, respValue, url)).build(); 
    }
    return Response.status(401).build();
  }

  @PUT
  @Path("addons/{addon}/properties/{property}")
  @Consumes({ "application/json" })
  @Produces({ "application/json" })
  public Response setProperty(final AddonProperties prop, @PathParam("addon") String addon, @PathParam("property") String property,  @Context HttpServletRequest request) {
    if (isAdmin(request)) {
      Properties.setPerProject(property, "perProject", prop.getPerProject());
      Properties.setEnabledProjects(property, "enabledProjects", prop.getEnabledProjects());
      return Response.noContent().build();
    }
    return Response.status(401).build();
  }

  private boolean isAdmin(HttpServletRequest request) {
    UserProfile userProfile = userManager.getRemoteUser(request);
    if (userProfile != null) {
      UserKey userKey = userProfile.getUserKey();
      if (userManager.isSystemAdmin(userKey)) {
        return true;
      }
    }
    return false;
  }


  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static final class AddonProperties
  {
    @XmlElement
    private boolean perProject;

    @XmlElement
    private List<String> enabledProjects;

    private AddonProperties() { } // JAXB needs this

    public AddonProperties(boolean perProject, List<String> enabledProjects)
    {
      this.perProject = perProject;
      this.enabledProjects = enabledProjects;
    }

    public boolean getPerProject()
    {
      return this.perProject;
    }

    public List<String> getEnabledProjects()
    {
      return this.enabledProjects;
    }

  }

  @XmlRootElement
  public static final class PropertiesResponse
  {
    @XmlElement
    private String key;

    @XmlElement
    private AddonProperties value;
    
    @XmlElement
    private String self;

    private PropertiesResponse() { } // JAXB needs this

    public PropertiesResponse(String key, AddonProperties value, String self)
    {
      this.key = key;
      this.value = value;
      this.self = self;
    }

  }

}