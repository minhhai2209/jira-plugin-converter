package minhhai2209.jirapluginconverter.plugin.rest;

import java.util.List;
import java.util.ArrayList;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;

// https://developer.atlassian.com/docs/atlassian-platform-common-components/shared-access-layer/sal-code-samples

public class Properties
{
  private static PluginSettingsFactory pluginSettingsFactory;

  public Properties(PluginSettingsFactory pluginSettingsFactory) {
    this.pluginSettingsFactory = pluginSettingsFactory;
  }

  public static boolean getPerProject(final String propertyKey, final String objectName) {
    final String perProject = (String)pluginSettingsFactory.createGlobalSettings().get("minhhai2209.jirapluginconverter-" + propertyKey + "." + objectName);
    return "1".equals(perProject);
  }

  public static void setPerProject(final String propertyKey, final String objectName, final boolean value) {
    pluginSettingsFactory.createGlobalSettings().put("minhhai2209.jirapluginconverter-" + propertyKey + "." + objectName, (Object)(value ? "1" : "0"));
  }

  public static List<String> getEnabledProjects(final String propertyKey, final String objectName) {
    final List<String> enabledProjects = (List<String>)pluginSettingsFactory.createGlobalSettings().get("minhhai2209.jirapluginconverter-" + propertyKey + "." + objectName);
    if(enabledProjects!=null){
      return enabledProjects;
    }else{
      final List<String> empty = new ArrayList<String>();
      return empty;
    }
  }

  public static void setEnabledProjects(final String propertyKey, final String objectName,  final List<String> value) {
    pluginSettingsFactory.createGlobalSettings().put("minhhai2209.jirapluginconverter-" + propertyKey + "." + objectName, value);
  }

}