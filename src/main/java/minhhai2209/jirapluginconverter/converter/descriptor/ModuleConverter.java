package minhhai2209.jirapluginconverter.converter.descriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;

public abstract class ModuleConverter<T, R> {

  public abstract T toPluginModule(R connectModule, Modules modules);
}
