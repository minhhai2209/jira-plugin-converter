package minhhai2209.jirapluginconverter.converter.descriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.tabpanel.TabPanel;
import minhhai2209.jirapluginconverter.plugin.descriptor.Param;
import minhhai2209.jirapluginconverter.plugin.descriptor.ProjectTabPanelModule;
import minhhai2209.jirapluginconverter.plugin.descriptor.Resource;

import java.util.ArrayList;
import java.util.List;

public class ProjectTabPanelConverter extends ModuleConverter<ProjectTabPanelModule, TabPanel> {

  @Override
  public ProjectTabPanelModule toPluginModule(TabPanel tabPanel, Modules modules) {

    String name = tabPanel.getName().getValue();

    List<Resource> resources = new ArrayList<Resource>();
    Resource resource = new Resource();
    resource.setType("velocity");
    resource.setName("view");
    resource.setLocation("templates/project-tab-panel.vm");
    resources.add(resource);

    List<Param> params = new ArrayList<Param>();
    Param param = new Param();
    param.setName("noTitle");
    param.setValue("false");
    params.add(param);

    ProjectTabPanelModule module = new ProjectTabPanelModule();
    module.setClazz("minhhai2209.jirapluginconverter.plugin.render.ProjectTabPanelRenderer");
    module.setKey(tabPanel.getKey());
    module.setName(name);
    module.setLabel(name);
    module.setDescription(name);
    module.setOrder(tabPanel.getWeight());
    module.setResources(resources);
    module.setParams(params);
    return module;
  }

}