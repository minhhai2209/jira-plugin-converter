package minhhai2209.jirapluginconverter.converter.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

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

  public static void replaceTextInDescriptor(File root, File connectDescriptorFile) {
    File pluginDescriptorFile = new File(root, "/src/main/resources/atlassian-plugin.xml");
    String placeholder = "<generated_xml />";
    String pluginDescriptor = DescriptorConverter.convert(connectDescriptorFile);
    replaceTextInFile(pluginDescriptorFile, placeholder, pluginDescriptor);
  }

  public static void copy(File root, File connectFile) {
    try {
      File destFile = new File(root, "/src/main/resources/imported_atlas_connect_descriptor.json");
      FileUtils.copyFile(connectFile, destFile);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
  }

  public static File getConnectFile(String url) throws IOException {
    URL source = new URL(url);
    String connectFileName = System.currentTimeMillis() + "-" + "connect.json";
    File connectFile = new File(connectFileName);
    FileUtils.copyURLToFile(source, connectFile);
    return connectFile;
  }
}
