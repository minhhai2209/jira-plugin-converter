package minhhai2209.jirapluginconverter.converter.utils;

import minhhai2209.jirapluginconverter.connect.descriptor.Descriptor;
import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.converter.descriptor.DescriptorConverter;
import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ConverterUtils {

  public static void replaceTextInFolder(File source, String oldText, String newText) {
    String sourceName = source.getName();
    if (sourceName.equals(".git") || sourceName.equals("target")) {
      return;
    } else if (source.isDirectory()) {
      File[] children = source.listFiles();
      for (int i = 0; i < children.length; i++) {
        replaceTextInFolder(children[i], oldText, newText);
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

  public static void replaceTextInConfigure(File root, Descriptor descriptor) {
    File pluginDescriptorFile = new File(root, "/src/main/resources/atlassian-plugin.xml");
    String placeholder = "<!-- <plugin_info_xml /> -->";
    String configureDescriptor = DescriptorConverter.convertPluginInfoXml(descriptor);
    if (configureDescriptor != null) {
      replaceTextInFile(pluginDescriptorFile, placeholder, configureDescriptor);
    }
  }

  public static void replaceNameSpace(File root, String groupId) throws IOException {
    String originalGroupId = "minhhai2209.jirapluginconverter";
    if (!originalGroupId.equals(groupId)) {
      File pluginDescriptorFile = new File(root, "/src/main/resources/atlassian-plugin.xml");
      replaceTextInFile(pluginDescriptorFile, originalGroupId, groupId);
      File newjavaSrcDir = renamePath(root, "/src/main/java", groupId);
      ConverterUtils.replaceTextInFolder(newjavaSrcDir, originalGroupId, groupId);
      renamePath(root, "/src/main/resources", groupId);
    }
  }

  private static File renamePath(File root, String parent, String groupId) throws IOException {
    File javaSrcDir = new File(root, parent + "/minhhai2209/jirapluginconverter");
    String newPackageFolder = "/" + groupId.replace(".", "/");
    File newjavaSrcDir = new File(root, parent + newPackageFolder);
    File newjavaSrcConverterDir = new File(root, parent + newPackageFolder + "/converter");
    FileUtils.moveDirectory(javaSrcDir, newjavaSrcDir);
    FileUtils.deleteDirectory(new File(root, parent + "/minhhai2209"));
    FileUtils.deleteDirectory(newjavaSrcConverterDir);
    return newjavaSrcDir;
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

  public static File getTemplate(String templatePath) {
    try {
      File outputFolder = new File(templatePath);
      File outputSrcFolder = new File(templatePath + "/src");
      FileUtils.copyDirectory(new File("src"), outputSrcFolder);
      FileUtils.copyFileToDirectory(new File("pom.xml"), outputFolder);
      return outputFolder;
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }
}
