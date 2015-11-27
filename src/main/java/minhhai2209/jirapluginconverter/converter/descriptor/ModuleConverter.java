package minhhai2209.jirapluginconverter.converter.descriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;

public abstract class ModuleConverter<T, R> {

  protected static String queryContext = "projectKey=${project.key}&versionId=${version.id}&componentId=${component.id}&issueId=${issue.id}&issueKey=${issue.key}";
  public abstract T toPluginModule(R connectModule, Modules modules);
}
