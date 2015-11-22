package minhhai2209.jirapluginconverter.converter;

import java.io.File;

import minhhai2209.jirapluginconverter.converter.utils.ConverterUtils;
import minhhai2209.jirapluginconverter.plugin.properties.PluginProperties;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class Converter {

  public static void generate(String templatePath, PluginProperties info) {

    String groupId = info.getGroupId();
    String artifactId = info.getArtifactId();

    try {
      File root = new File(templatePath);
      File connectFile = ConverterUtils.getConnectFile(info.getUrl());
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_id", artifactId);
      ConverterUtils.replaceTextInFolder(root, "generated_group_id", groupId);
      ConverterUtils.replaceTextInFolder(root, "generated_company_name", info.getCompany());
      ConverterUtils.replaceTextInFolder(root, "generated_company_url", info.getCompanyUrl());
      ConverterUtils.replaceTextInFolder(root, "generated_description", info.getDescription());
      ConverterUtils.replaceTextInDescriptor(root, connectFile);
      ConverterUtils.copy(root, connectFile);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
}
