package minhhai2209.jirapluginconverter.converter.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.zeroturnaround.zip.ZipUtil;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.converter.descriptor.DescriptorConverter;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class ConverterUtils {

  public static void replaceTextInFolder(File source, String oldText, String newText) {
    String sourceName = source.getName();
    if (sourceName.equals(".git") || sourceName.equals("target")) {
      return;
    } else if (source.isDirectory()) {
      File[] childs = source.listFiles();
      for (int i = 0; i < childs.length; i++) {
        replaceTextInFolder(childs[i], oldText, newText);
      }
    } else if (source.isFile()) {
      replaceTextInFile(source, oldText, newText);
    }
  }

  private static void replaceTextInFile(File source, String oldText, String newText) {
    try {
      String fileContent = FileUtils.readFileToString(source);
      fileContent = fileContent.replace(oldText, newText);
      FileUtils.write(source, fileContent);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  public static void replaceTextInDescriptor(File root, Modules modules) {
    File pluginDescriptorFile = new File(root, "/src/main/resources/atlassian-plugin.xml");
    String placeholder = "<!-- <generated_xml /> -->";
    String pluginDescriptor = DescriptorConverter.convert(modules);
    replaceTextInFile(pluginDescriptorFile, placeholder, pluginDescriptor);
  }

  public static void replaceTextInConfigure(File root, Modules modules) {
    File pluginDescriptorFile = new File(root, "/src/main/resources/atlassian-plugin.xml");
    String placeholder = "<!-- <configure_xml /> -->";
    String configureDescriptor = DescriptorConverter.convertConfigurePage(modules.getConfigurePage());
    if (configureDescriptor != null) {
      replaceTextInFile(pluginDescriptorFile, placeholder, configureDescriptor);
    }
  }

  public static void copy(File root, String connectFile) {
    try {
      File destFile = new File(root, "/src/main/resources/imported_atlas_connect_descriptor.json");
      FileUtils.write(destFile, connectFile);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  public static String getConnectFile(String url) {
    try {
      URL source = new URL(url);
      InputStream in = null;
      try {
        in = source.openStream();
        return IOUtils.toString(in);
      } finally {
        if (in != null) {
          IOUtils.closeQuietly(in);
        }
      }
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  public static File getTemplate(String templatePath, String version) {
    try {
      URL source = new URL("https://github.com/minhhai2209/jira-plugin-converter/archive/" + version + ".zip");
      File zip = new File(templatePath);
      FileUtils.copyURLToFile(source, zip);
      ZipUtil.explode(zip);
      File template = new File(templatePath, "jira-plugin-converter-master");
      return template;
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }
}
