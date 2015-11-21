package minhhai2209.jirapluginconverter.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.PackageUtils;

@Service
public class GenerateService {

  private String templateFolder;

  public File generate(GenerateInfo info) {
    String groupId = info.getGroupId();
    String artifactId = info.getArtifactId();

    try {
      File root = getCloneTemplate(groupId, artifactId);
      File group = new File(root, "\\src\\main\\java\\generated_group_id");
      File connectFile = getConnectFile(info.getUrl());
      PackageUtils.replaceFolder(group, groupId);
      PackageUtils.replaceTextInFolder(root, "generated_artifact_id", artifactId);
      PackageUtils.replaceTextInFolder(root, "generated_group_id", groupId);
      PackageUtils.replaceTextInFolder(root, "generated_company_name", info.getCompany());
      PackageUtils.replaceTextInFolder(root, "generated_company_url", info.getCompanyUrl());
      PackageUtils.replaceTextInFolder(root, "generated_description", info.getDescription());
      PackageUtils.replaceTextInDescriptor(root, connectFile);
      PackageUtils.copy(root, connectFile);
      String packagePath = this.getClass().getClassLoader().getResource("package.bat").getFile();
      File packageFile = new File(packagePath);
      System.out.println(packageFile.getAbsolutePath());
      Process p = Runtime.getRuntime().exec("cmd /c call C:\\jira-plugin-generator\\git\\jira-plugin-generator-server\\src\\main\\resources\\package.bat ", null, root);
      IOUtils.toString(p.getInputStream());
      IOUtils.toString(p.getErrorStream());
      p.waitFor();
//      File zipped = zip(root, artifactId);
      File jar = new File(root, "target\\" +  artifactId + "-1.0-SNAPSHOT.jar");
      // will be deleted later
      // FileUtils.deleteDirectory(root);
      return jar;
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  private File getConnectFile(String url) throws IOException {
    URL source = new URL(url);
    String fileName = System.currentTimeMillis() + "-" + "connect.json";
    File f = new File(fileName);

    FileUtils.copyURLToFile(source, f);
    return f;
  }

  private File getCloneTemplate(String groupId, String artifactId) throws IOException {
    final String templatePath = templateFolder;
    File template = new File(templatePath);

    if (!template.isDirectory()) {
      throw new IllegalStateException("Invalid template folder");
    }

    String cloneFolder = groupId + "-" + artifactId + "-" + System.currentTimeMillis();
    File clone = new File(cloneFolder);
    if (clone.exists()) {
      FileUtils.deleteDirectory(clone);
    }
    clone.mkdir();

    FileUtils.copyDirectory(template, clone);
    return clone;
  }
}
