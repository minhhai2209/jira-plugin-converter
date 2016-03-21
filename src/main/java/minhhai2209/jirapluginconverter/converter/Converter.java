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
      File root = ConverterUtils.getTemplate(templatePath);
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_id", info.getArtifactId());
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_name", info.getArtifactName());
      ConverterUtils.replaceTextInFolder(root, "generated_group_id", info.getGroupId());
      ConverterUtils.replaceTextInFolder(root, "generated_company_name", info.getCompany());
      ConverterUtils.replaceTextInFolder(root, "generated_company_url", info.getCompanyUrl());
      ConverterUtils.replaceTextInFolder(root, "generated_description", info.getDescription());

      String connectFile = ConverterUtils.getConnectFile(info.getUrl());
      Descriptor descriptor = DescriptorConverter.analyze(connectFile);
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
    info.setArtifactId(args[0]);
    info.setArtifactName(args[1]);
    info.setGroupId(args[2]);
    info.setCompany(args[3]);
    info.setCompanyUrl(args[4]);
    info.setDescription(args[5]);
    info.setUrl(args[6]);
    generate(args[7], info);
  }
}
