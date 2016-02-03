package minhhai2209.jirapluginconverter.plugin.setting;

import minhhai2209.jirapluginconverter.connect.descriptor.LifeCycle;

public class LifeCycleUtils {

  public static String getInstalledUri() {
    LifeCycle lifeCycle = PluginSetting.getDescriptor().getLifecycle();
    String uri = lifeCycle == null ? null : lifeCycle.getInstalled();
    return uri;
  }

  public static String getDisabledUri() {
    LifeCycle lifeCycle = PluginSetting.getDescriptor().getLifecycle();
    String uri = lifeCycle == null ? null : lifeCycle.getDisabled();
    return uri;
  }

  public static String getUninstalledUri() {
    LifeCycle lifeCycle = PluginSetting.getDescriptor().getLifecycle();
    String uri = lifeCycle == null ? null : lifeCycle.getUninstalled();
    return uri;
  }

  public static String getEnabledUri() {
    LifeCycle lifeCycle = PluginSetting.getDescriptor().getLifecycle();
    String uri = lifeCycle == null ? null : lifeCycle.getEnabled();
    return uri;
  }
}
