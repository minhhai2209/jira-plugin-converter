package generated_group_id.utils.http;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

  public static String getModuleKey(HttpServletRequest request) {
    String servletPath = request.getServletPath();
    String requestUrl = request.getRequestURL().toString();
    String path = StringUtils.substringAfter(requestUrl, servletPath + "/");
    return path;
  }
}
