package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;

public class Report {

  private I18nProperty name;
  private String key;
  private I18nProperty description;
  private String url;
  private String thumbnailUrl;
  private int weight = 100;
  private ReportCategory reportCategory = ReportCategory.other;
  
  public I18nProperty getName() {
    return name;
  }
  public void setName(I18nProperty name) {
    this.name = name;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public I18nProperty getDescription() {
    return description;
  }
  public void setDescription(I18nProperty description) {
    this.description = description;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }
  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }
  public int getWeight() {
    return weight;
  }
  public void setWeight(int weight) {
    this.weight = weight;
  }
  public ReportCategory getReportCategory() {
    return reportCategory;
  }
  public void setReportCategory(ReportCategory reportCategory) {
    this.reportCategory = reportCategory;
  }
}
