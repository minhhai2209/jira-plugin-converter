package minhhai2209.jirapluginconverter.converter;

public abstract class Converter<T, R> {
  
  protected static String queryContext = "projectKey=${project.key}&amp;versionId=${version.id}&amp;componentId=${component.id}&amp;issueId=${issue.id}&amp;issueKey=${issue.key}";
  public abstract T toPluginModule(R connectModule);
}
