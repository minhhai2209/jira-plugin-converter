package minhhai2209.jirapluginconverter.connect.descriptor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import minhhai2209.jirapluginconverter.connect.descriptor.jira.DashboardItem;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.EntityProperty;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.ProjectAdminTabPanel;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.Report;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.SearchRequestView;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.TabPanel;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.WorkflowPostFuntion;
import minhhai2209.jirapluginconverter.connect.descriptor.page.Page;
import minhhai2209.jirapluginconverter.connect.descriptor.webhook.Webhook;
import minhhai2209.jirapluginconverter.connect.descriptor.webitem.WebItem;
import minhhai2209.jirapluginconverter.connect.descriptor.webpanel.WebPanel;
import minhhai2209.jirapluginconverter.connect.descriptor.websection.WebSection;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Module {

  private List<WebItem> webItems;
  private List<WebPanel> webPanels;
  private List<WebSection> webSections;
  private List<Page> generalPages ;
  private List<Page> adminPages;
  private List<Page> profilePages;
  private List<Page> configurePage;
  private List<Webhook> webhooks;
  private List<DashboardItem> jiraDashboardItems;
  private List<EntityProperty> jiraEntityProperties;
  private List<ProjectAdminTabPanel> jiraProjectAdminTabPanels;
  private List<Report> jiraReports;
  private List<SearchRequestView> jiraSearchRequestViews;
  private List<TabPanel> jiraIssueTabPanels;
  private List<TabPanel> jiraProjectTabPanels;
  private List<TabPanel> jiraProfileTabPanels;
  private List<WorkflowPostFuntion> jiraWorkflowPostFunctions;
  
  public List<WebItem> getWebItems() {
    return webItems;
  }

  public void setWebItems(List<WebItem> webItems) {
    this.webItems = webItems;
  }

  public List<WebPanel> getWebPanels() {
    return webPanels;
  }

  public void setWebPanels(List<WebPanel> webPanels) {
    this.webPanels = webPanels;
  }

  public List<WebSection> getWebSections() {
    return webSections;
  }

  public void setWebSections(List<WebSection> webSections) {
    this.webSections = webSections;
  }

  public List<Page> getGeneralPages() {
    return generalPages;
  }

  public void setGeneralPages(List<Page> generalPages) {
    this.generalPages = generalPages;
  }

  public List<Page> getAdminPages() {
    return adminPages;
  }

  public void setAdminPages(List<Page> adminPages) {
    this.adminPages = adminPages;
  }

  public List<Page> getProfilePages() {
    return profilePages;
  }

  public void setProfilePages(List<Page> profilePages) {
    this.profilePages = profilePages;
  }

  public List<Page> getConfigurePage() {
    return configurePage;
  }

  public void setConfigurePage(List<Page> configurePage) {
    this.configurePage = configurePage;
  }

  public List<Webhook> getWebhooks() {
    return webhooks;
  }

  public void setWebhooks(List<Webhook> webhooks) {
    this.webhooks = webhooks;
  }

  public List<DashboardItem> getJiraDashboardItems() {
    return jiraDashboardItems;
  }

  public void setJiraDashboardItems(List<DashboardItem> jiraDashboardItems) {
    this.jiraDashboardItems = jiraDashboardItems;
  }

  public List<EntityProperty> getJiraEntityProperties() {
    return jiraEntityProperties;
  }

  public void setJiraEntityProperties(List<EntityProperty> jiraEntityProperties) {
    this.jiraEntityProperties = jiraEntityProperties;
  }

  public List<ProjectAdminTabPanel> getJiraProjectAdminTabPanels() {
    return jiraProjectAdminTabPanels;
  }

  public void setJiraProjectAdminTabPanels(List<ProjectAdminTabPanel> jiraProjectAdminTabPanels) {
    this.jiraProjectAdminTabPanels = jiraProjectAdminTabPanels;
  }

  public List<Report> getJiraReports() {
    return jiraReports;
  }

  public void setJiraReports(List<Report> jiraReports) {
    this.jiraReports = jiraReports;
  }

  public List<SearchRequestView> getJiraSearchRequestViews() {
    return jiraSearchRequestViews;
  }

  public void setJiraSearchRequestViews(List<SearchRequestView> jiraSearchRequestViews) {
    this.jiraSearchRequestViews = jiraSearchRequestViews;
  }

  public List<TabPanel> getJiraIssueTabPanels() {
    return jiraIssueTabPanels;
  }

  public void setJiraIssueTabPanels(List<TabPanel> jiraIssueTabPanels) {
    this.jiraIssueTabPanels = jiraIssueTabPanels;
  }

  public List<TabPanel> getJiraProjectTabPanels() {
    return jiraProjectTabPanels;
  }

  public void setJiraProjectTabPanels(List<TabPanel> jiraProjectTabPanels) {
    this.jiraProjectTabPanels = jiraProjectTabPanels;
  }

  public List<TabPanel> getJiraProfileTabPanels() {
    return jiraProfileTabPanels;
  }

  public void setJiraProfileTabPanels(List<TabPanel> jiraProfileTabPanels) {
    this.jiraProfileTabPanels = jiraProfileTabPanels;
  }

  public List<WorkflowPostFuntion> getJiraWorkflowPostFunctions() {
    return jiraWorkflowPostFunctions;
  }

  public void setJiraWorkflowPostFunctions(List<WorkflowPostFuntion> jiraWorkflowPostFunctions) {
    this.jiraWorkflowPostFunctions = jiraWorkflowPostFunctions;
  }
}
