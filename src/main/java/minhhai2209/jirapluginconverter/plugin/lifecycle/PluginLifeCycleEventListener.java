package minhhai2209.jirapluginconverter.plugin.lifecycle;

import org.springframework.beans.factory.DisposableBean;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;

import minhhai2209.jirapluginconverter.plugin.setting.PluginSetting;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class PluginLifeCycleEventListener implements DisposableBean {

  private EventType currentPluginStatus = null;

  private boolean registered = false;

  private PluginLifeCycleEventHandler pluginLifeCycleEventHandler;

  public PluginLifeCycleEventListener(PluginLifeCycleEventHandler pluginLifeCycleEventHandler) {
    this.pluginLifeCycleEventHandler = pluginLifeCycleEventHandler;
    register();
  }

  private void handle(EventType nextPluginStatus, Plugin plugin) {
    if (plugin != null && PluginSetting.PLUGIN_KEY.equals(plugin.getKey())) {
      if (EventType.uninstalled.equals(currentPluginStatus)) {
        deregister();
        return;
      } else if (currentPluginStatus == null) {
        if (EventType.enabled.equals(nextPluginStatus)) {
          setPluginStatus(EventType.installed, plugin);
        } else {
          setPluginStatus(nextPluginStatus, plugin);
        }
      } else {
        if (currentPluginStatus.equals(nextPluginStatus)) {
          return;
        } else {
          setPluginStatus(nextPluginStatus, plugin);
        }
      }
      if (EventType.uninstalled.equals(currentPluginStatus)) {
        deregister();
      }
    }
  }

  private void deregister() {
    try {
      if (registered) {
        PluginEventManager pluginEventManager = ComponentAccessor.getComponent(PluginEventManager.class);
        pluginEventManager.unregister(this);
        registered = false;
      }
    } catch (Exception e) {
    }
  }

  private void register() {
    try {
      if (!registered) {
        PluginEventManager pluginEventManager = ComponentAccessor.getComponent(PluginEventManager.class);
        pluginEventManager.register(this);
        registered = true;
      }
    } catch (Exception e) {
    }
  }

  private void setPluginStatus(EventType nextPluginStatus, Plugin plugin) {
    try {
      currentPluginStatus = nextPluginStatus;
      switch (currentPluginStatus) {
        case installed:
          pluginLifeCycleEventHandler.onInstalled(plugin);
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
      ExceptionUtils.throwUnchecked(e);
    }
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
    handle(EventType.disabled, null);
  }
}