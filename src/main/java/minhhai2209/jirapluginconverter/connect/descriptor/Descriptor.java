package minhhai2209.jirapluginconverter.connect.descriptor;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import minhhai2209.jirapluginconverter.connect.descriptor.authentication.Authentication;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=Include.NON_NULL)
public class Descriptor {

  private String key;
  private String name;
  private String version;
  private String description;
  private Map<String, String> vendor;
  private Map<String, String> links;
  private LifeCycle lifecycle;
  private String baseUrl;
  private Authentication authentication;
  private boolean enableLicensing;
  private Module modules;
  private List<String> scopes;

  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public Map<String, String> getVendor() {
    return vendor;
  }
  public void setVendor(Map<String, String> vendor) {
    this.vendor = vendor;
  }
  public Map<String, String> getLinks() {
    return links;
  }
  public void setLinks(Map<String, String> links) {
    this.links = links;
  }
  public LifeCycle getLifecycle() {
    return lifecycle;
  }
  public void setLifecycle(LifeCycle lifecycle) {
    this.lifecycle = lifecycle;
  }
  public String getBaseUrl() {
    return baseUrl;
  }
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
  public Authentication getAuthentication() {
    return authentication;
  }
  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
  }
  public boolean isEnableLicensing() {
    return enableLicensing;
  }
  public void setEnableLicensing(boolean enableLicensing) {
    this.enableLicensing = enableLicensing;
  }
  public Module getModules() {
    return modules;
  }
  public void setModules(Module modules) {
    this.modules = modules;
  }
  public List<String> getScopes() {
    return scopes;
  }
  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

}
