package minhhai2209.jirapluginconverter.plugin.setting;

import minhhai2209.jirapluginconverter.connect.descriptor.authentication.Authentication;
import minhhai2209.jirapluginconverter.plugin.utils.EnumUtils;

public class AuthenticationUtils {

  public static boolean needsAuthentication() {
    Authentication authentication = PluginSetting.getDescriptor().getAuthentication();
    return authentication != null &&
        EnumUtils.equals(authentication.getType(), minhhai2209.jirapluginconverter.connect.descriptor.authentication.Type.jwt);
  }

}
