package hope.generator.utils;

import java.io.File;

import org.junit.Test;

import minhhai2209.jirapluginconverter.utils.PluginUtils;

public class XmlTest {

  @Test
  public void test() throws Exception {
    File connectFile = new File("D:\\jira-plugin\\hello-world\\src\\main\\resources\\atlassian-connect.json");
    String pluginDescriptor = PluginUtils.getPluginDescriptor(connectFile);
    System.out.println(pluginDescriptor);
  }
}
