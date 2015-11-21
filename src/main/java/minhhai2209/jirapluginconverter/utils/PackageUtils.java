package minhhai2209.jirapluginconverter.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class PackageUtils {

  public static void replaceFolder(File sourceFolder, String packageName) {

    try {
      File group = createGroup(sourceFolder, packageName);
      FileUtils.moveDirectory(sourceFolder, group);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  public static void replaceTextInFolder(File source, String oldText, String newText) {
    if (source.isDirectory()) {
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

  public static void replaceTextInDescriptor(File root, File connectDescriptorFile) {
    File pluginDescriptorFile = new File(root, "/src/main/resources/atlassian-plugin.xml");
    String placeholder = "<generated_xml />";
    String pluginDescriptor = PluginUtils.getPluginDescriptor(connectDescriptorFile);
    replaceTextInFile(pluginDescriptorFile, placeholder, pluginDescriptor);
  }

  private static File createGroup(File sourceFolder, String groupId) {

    String[] groups = groupId.split("\\.");
    File root = sourceFolder.getParentFile();
    for (String group : groups) {
      root = new File(root, group);
    }
    return root;
  }

  public static void copy(File root, File connectFile) {
    try {
      File destFile = new File(root, "/src/main/resources/imported_atlas_connect_descriptor.json");
      FileUtils.copyFile(connectFile, destFile);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }
}
