package minhhai2209.jirapluginconverter.converter;

import java.io.File;

import minhhai2209.jirapluginconverter.converter.utils.ConverterUtils;
import minhhai2209.jirapluginconverter.plugin.properties.PluginProperties;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class Converter {

  public static void generate(String templatePath, PluginProperties info) {

    try {
      File root = new File(templatePath);
      String connectFile = ConverterUtils.getConnectFile(info.getUrl());
      ConverterUtils.replaceTextInFolder(root, "generated_artifact_id", info.getArtifactId());
      ConverterUtils.replaceTextInFolder(root, "generated_group_id", info.getGroupId());
      ConverterUtils.replaceTextInFolder(root, "generated_company_name", info.getCompany());
      ConverterUtils.replaceTextInFolder(root, "generated_company_url", info.getCompanyUrl());
      ConverterUtils.replaceTextInFolder(root, "generated_description", info.getDescription());
      ConverterUtils.replaceTextInDescriptor(root, connectFile);
      ConverterUtils.copy(root, connectFile);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  public static void main(String[] args) {

    PluginProperties info = new PluginProperties();
    info.setArtifactId("artifactid");
    info.setGroupId("groupid");
    info.setCompany("company");
    info.setCompanyUrl("http://localhost:7777/hw/company");
    info.setDescription("description");

    String templatePath = "D:\tmp\jira-plugin-converter";

    generate(templatePath, info);
  }
}
