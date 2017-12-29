package minhhai2209.jirapluginconverter.plugin.lifecycle;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;
import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import org.springframework.beans.factory.DisposableBean;

import java.util.UUID;

public class PluginLifeCycleEventListener implements DisposableBean {

  private EventType currentPluginStatus = null;

  private boolean registered = false;

  private String source = UUID.randomUUID().toString();

  private boolean newlyInstalled = true;

  private PluginLifeCycleEventHandler pluginLifeCycleEventHandler;

  public PluginLifeCycleEventListener(PluginLifeCycleEventHandler pluginLifeCycleEventHandler) {

    PluginSetting.readDescriptor();
    this.pluginLifeCycleEventHandler = pluginLifeCycleEventHandler;
    register();
  }

  private void handle(EventType nextPluginStatus, Plugin plugin) {
    PluginSetting.setJiraPlugin(plugin);
    if (currentPluginStatus == null && EventType.enabled.equals(nextPluginStatus)) {
      fireNullToEnabledEvent(plugin);
    }
    if (plugin == null || PluginSetting.getDescriptor().getKey().equals(plugin.getKey())) {
      log("current " + currentPluginStatus + " next " + nextPluginStatus + " " + newlyInstalled);
      if (EventType.uninstalled.equals(currentPluginStatus)) {
        unregister();
        return;
      } else if (EventType.disabled.equals(currentPluginStatus) && EventType.enabled.equals(nextPluginStatus)) {
        if (newlyInstalled) {
          setPluginStatus(EventType.enabled, plugin);
        } else {
          fireDisabledToEnabledEvent(plugin);
          unregister();
          return;
        }
      } else if (currentPluginStatus == null) {
        if (EventType.enabled.equals(nextPluginStatus)) {
          setPluginStatus(EventType.installed, plugin);
        } else {
          setPluginStatus(nextPluginStatus, plugin);
        }
      } else if (!currentPluginStatus.equals(nextPluginStatus)) {
        setPluginStatus(nextPluginStatus, plugin);
      }
      if (EventType.uninstalled.equals(currentPluginStatus)) {
        unregister();
      }
    }
  }

  private void log(String message) {
    System.out.println(source + " " + message);
  }

  private void fireNullToEnabledEvent(Plugin plugin) {
    log("fire null to enabled");
    PluginNullToEnabledEvent event = new PluginNullToEnabledEvent(plugin, source);
    PluginEventManager pluginEventManager = getPluginEventManager();
    pluginEventManager.broadcast(event);
  }

  private void fireDisabledToEnabledEvent(Plugin plugin) {
    log("fire disabled to enabled");
    PluginDisabledToEnabledEvent event = new PluginDisabledToEnabledEvent(plugin, source);
    PluginEventManager pluginEventManager = getPluginEventManager();
    pluginEventManager.broadcast(event);
  }

  private PluginEventManager getPluginEventManager() {
    PluginEventManager pluginEventManager = ComponentAccessor.getComponent(PluginEventManager.class);
    return pluginEventManager;
  }

  private void unregister() {
    try {
      if (registered) {
        PluginEventManager pluginEventManager = getPluginEventManager();
        pluginEventManager.unregister(this);
        registered = false;
      }
    } catch (Exception e) {
      log(e.getMessage());
    }
  }

  private void register() {
    try {
      if (!registered) {
        PluginEventManager pluginEventManager = getPluginEventManager();
        pluginEventManager.register(this);
        registered = true;
      }
    } catch (Exception e) {
    }
  }

  private void setPluginStatus(EventType nextPluginStatus, Plugin plugin) {
    log("status " + currentPluginStatus + " to " + nextPluginStatus);
    try {
      newlyInstalled = false;
      currentPluginStatus = nextPluginStatus;
      switch (currentPluginStatus) {
        case installed:
          pluginLifeCycleEventHandler.onInstalled(null);
          break;
        case uninstalled:
          pluginLifeCycleEventHandler.onUninstalled();
          break;
        case enabled:
          pluginLifeCycleEventHandler.onEnabled();
          break;
        case disabled:
          pluginLifeCycleEventHandler.onDisabled();
          break;
        default:
          throw new IllegalArgumentException();
      }
    } catch (Exception e) {
      log(e.getMessage());
      ExceptionUtils.throwUnchecked(e);
    }
  }

  @PluginEventListener
  public void onPluginDisabledToEnabled(PluginDisabledToEnabledEvent event) {
    if (event == null) return;
    log("pre receive disabled to enabled");
    if (!source.equals(event.getSource())) {
      log("receive disabled to enabled");
      currentPluginStatus = EventType.disabled;
    }
  }

  @PluginEventListener
  public void onPluginNullToEnabled(PluginNullToEnabledEvent event) {
    if (event == null) return;
    log("pre receive null to enabled");
    if (!source.equals(event.getSource())) {
      log("receive null to enabled");
      handle(EventType.enabled, event.getPlugin());
    }
  }

  @PluginEventListener
  public void onPluginFrameworkShutdown(PluginFrameworkShutdownEvent event) {
    handle(EventType.disabled, null);
  }

  @PluginEventListener
  public void onPluginEnabled(PluginEnabledEvent event) {
    if (event == null) return;
    handle(EventType.enabled, event.getPlugin());
  }

  @PluginEventListener
  public void onPluginDisabled(PluginDisabledEvent event) {
    if (event == null) return;
    handle(EventType.disabled, event.getPlugin());
  }

  @PluginEventListener
  public void onPluginUninstalledEvent(PluginUninstalledEvent event) {
    if (event == null) return;
    handle(EventType.uninstalled, event.getPlugin());
  }

  @Override
  public void destroy() throws Exception {
    handle(EventType.uninstalled, null);
  }
}
