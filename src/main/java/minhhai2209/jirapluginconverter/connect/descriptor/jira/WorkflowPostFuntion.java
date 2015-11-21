package minhhai2209.jirapluginconverter.connect.descriptor.jira;

import minhhai2209.jirapluginconverter.connect.descriptor.I18nProperty;

public class WorkflowPostFuntion {

  private I18nProperty name;
  private String key;
  private URL triggered;
  private URL create;
  private I18nProperty description;
  private URL edit;
  private URL view;
  
  public static class URL {
    public String url;
  }

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

  public URL getTriggered() {
    return triggered;
  }

  public void setTriggered(URL triggered) {
    this.triggered = triggered;
  }

  public URL getCreate() {
    return create;
  }

  public void setCreate(URL create) {
    this.create = create;
  }

  public I18nProperty getDescription() {
    return description;
  }

  public void setDescription(I18nProperty description) {
    this.description = description;
  }

  public URL getEdit() {
    return edit;
  }

  public void setEdit(URL edit) {
    this.edit = edit;
  }

  public URL getView() {
    return view;
  }

  public void setView(URL view) {
    this.view = view;
  }
}
