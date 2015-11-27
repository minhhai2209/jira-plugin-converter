package minhhai2209.jirapluginconverter.plugin.setting;

import minhhai2209.jirapluginconverter.connect.descriptor.LifeCycle;

public class LifeCycleUtils {

  public static String getInstalledUrl() {
    String installedUri = getInstalledUri();
    return installedUri == null ? null : PluginSetting.getPluginBaseUrl() + installedUri;
  }

  public static String getInstalledUri() {
    LifeCycle lifeCycle = PluginSetting.getDescriptor().getLifecycle();
    String installedUri = lifeCycle == null ? null : lifeCycle.getInstalled();
    return installedUri;
  }
}
