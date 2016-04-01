package minhhai2209.jirapluginconverter.converter;

import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.converter.descriptor.DescriptorConverter;
import minhhai2209.jirapluginconverter.converter.utils.ConverterUtils;
import minhhai2209.jirapluginconverter.plugin.properties.PluginProperties;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

import java.io.File;

public class Converter {

  public static void generate(String templatePath, PluginProperties info) {

    try {
      String connectFile = ConverterUtils.getConnectFile(info.getUrl());
      Descriptor descriptor = DescriptorConverter.analyze(connectFile);

      File root = ConverterUtils.getTemplate(templatePath);
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_id", descriptor.getKey());
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_version", descriptor.getVersion());
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_name", descriptor.getName());
      ConverterUtils.replaceTextInFolder(root, "generated_group_id", info.getGroupId());
      ConverterUtils.replaceTextInFolder(root, "generated_company_name", descriptor.getVendor().get("name"));
      ConverterUtils.replaceTextInFolder(root, "generated_company_url", descriptor.getVendor().get("url"));
      ConverterUtils.replaceTextInFolder(root, "generated_description", descriptor.getDescription());
      ConverterUtils.replaceTextInDescriptor(root, descriptor.getModules());
      ConverterUtils.replaceTextInConfigure(root, descriptor);
      ConverterUtils.replaceNameSpace(root, info.getGroupId());

      ConverterUtils.copy(root, connectFile);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  public static void main(String[] args) {

    PluginProperties info = new PluginProperties();
    info.setGroupId(args[0]);
    info.setUrl(args[1]);
    generate(args[2], info);
  }
}
