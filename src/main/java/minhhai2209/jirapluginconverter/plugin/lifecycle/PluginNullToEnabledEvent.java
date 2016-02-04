package minhhai2209.jirapluginconverter.plugin.lifecycle;

import com.atlassian.plugin.Plugin;

public class PluginNullToEnabledEvent {

  private Plugin plugin;

  private String source;

  public PluginNullToEnabledEvent(Plugin plugin, String source) {
    this.plugin = plugin;
    this.source = source;
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public String getSource() {
    return source;
  }
}
