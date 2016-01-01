package minhhai2209.jirapluginconverter.converter.descriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;
import minhhai2209.jirapluginconverter.plugin.descriptor.IssueTabPanelModule;
import minhhai2209.jirapluginconverter.plugin.render.IssueTabPanelRenderer;

public class IssueTabPanelConverter extends ModuleConverter<IssueTabPanelModule, TabPanel> {

  @Override
  public IssueTabPanelModule toPluginModule(TabPanel tabPanel, Modules modules) {
    IssueTabPanelModule module = new IssueTabPanelModule();
    module.setClazz(IssueTabPanelRenderer.class.getName());
    module.setKey(tabPanel.getKey());
    module.setLabel(tabPanel.getName().getValue());
    module.setSupportsAjaxLoad(true);
    return module;
  }

}
