package hope.generator.utils;

import java.io.File;

import org.junit.Test;

import minhhai2209.jirapluginconverter.utils.PackageUtils;

public class GeneratorTest {

  @Test
  public void test() throws Exception {
    File root = new File("D:\\jira-plugin\\test\\1");
    File group = new File(root, "\\src\\main\\java\\generated_group_id");
    File connectFile = new File("D:\\jira-plugin\\hello-world\\src\\main\\resources\\atlassian-connect.json");
    PackageUtils.replaceFolder(group, "hope.test");
    PackageUtils.replaceTextInFolder(root, "generated_artifact_id", "TestPlugin");
    PackageUtils.replaceTextInFolder(root, "generated_group_id", "hope.test");
    PackageUtils.replaceTextInDescriptor(root, connectFile);
    PackageUtils.copy(root, connectFile);
  }

}
