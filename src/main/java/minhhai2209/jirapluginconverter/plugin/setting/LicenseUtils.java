package minhhai2209.jirapluginconverter.plugin.setting;

import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;

import java.util.NoSuchElementException;

public class LicenseUtils {

  private static PluginLicenseManager pluginLicenseManager;

  private static boolean enableLicensing;

  public static void setPluginLicenseManager(PluginLicenseManager pluginLicenseManager) {
    LicenseUtils.pluginLicenseManager = pluginLicenseManager;
    enableLicensing = PluginSetting.getDescriptor().isEnableLicensing();
  }

  public static final String getLic() {
    String lic;
    if (enableLicensing) {
      Option<PluginLicense> option = pluginLicenseManager.getLicense();
      try {
        PluginLicense license = option.get();
        if (license.isActive() && license.isValid()) {
          lic = "active";
        } else {
          lic = "none";
        }
      } catch (NoSuchElementException e) {
        lic = "active";
      }
    } else {
      lic = "none";
    }
    return lic;
  }
}
