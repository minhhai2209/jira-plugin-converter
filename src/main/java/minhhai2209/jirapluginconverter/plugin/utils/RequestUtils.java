package minhhai2209.jirapluginconverter.plugin.utils;

import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

  public static String getModuleKey(HttpServletRequest request) {
    String servletPath = request.getServletPath();
    String requestUrl = request.getRequestURL().toString();
    String path = StringUtils.substringAfter(requestUrl, servletPath + "/");
    path = path.replace(PluginSetting.getDescriptor().getKey() + "__", "");
    return path;
  }
}
