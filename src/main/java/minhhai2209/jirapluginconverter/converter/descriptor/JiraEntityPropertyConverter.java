package minhhai2209.jirapluginconverter.converter.descriptor;

import minhhai2209.jirapluginconverter.connect.descriptor.Modules;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.EntityProperty;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.EntityType;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.IndexKeyConfiguration;
import minhhai2209.jirapluginconverter.connect.descriptor.jira.PropertyIndex;
import minhhai2209.jirapluginconverter.plugin.descriptor.IndexDocumentConfiguration;
import minhhai2209.jirapluginconverter.plugin.descriptor.IndexDocumentKeyConfiguration;
import minhhai2209.jirapluginconverter.plugin.descriptor.IndexDocumentKeyExtractConfiguration;

import java.util.ArrayList;
import java.util.List;

public class JiraEntityPropertyConverter extends ModuleConverter<IndexDocumentConfiguration, EntityProperty> {

  @Override
  public IndexDocumentConfiguration toPluginModule(EntityProperty entityProperty, Modules modules) {

    List<IndexDocumentKeyConfiguration> keyConfigurations = new ArrayList<IndexDocumentKeyConfiguration>();

    for (IndexKeyConfiguration indexKeyConfiguration : entityProperty.getKeyConfigurations()) {

      List<IndexDocumentKeyExtractConfiguration> extractions = new ArrayList<IndexDocumentKeyExtractConfiguration>();
      for (PropertyIndex propertyIndex : indexKeyConfiguration.getExtractions()) {
        IndexDocumentKeyExtractConfiguration extraction = new IndexDocumentKeyExtractConfiguration();
        extraction.setPath(propertyIndex.getObjectName());
        extraction.setType(propertyIndex.getType().name().toLowerCase());
        extractions.add(extraction);
      }

      IndexDocumentKeyConfiguration keyConfiguration = new IndexDocumentKeyConfiguration();
      keyConfiguration.setPropertyKey(indexKeyConfiguration.getPropertyKey());
      keyConfiguration.setExtractions(extractions);
      keyConfigurations.add(keyConfiguration);
    }

    String entityKey;
    EntityType entityType = entityProperty.getEntityType();
    switch (entityType) {
      case issue:
      case ISSUE:
        entityKey = "IssueProperty";
        break;
      case user:
      case USER:
        entityKey = "UserProperty";
        break;
      case project:
      case PROJECT:
        entityKey = "ProjectProperty";
        break;
      default:
        throw new IllegalStateException();
    }

    IndexDocumentConfiguration configuration = new IndexDocumentConfiguration();
    configuration.setEntityKey(entityKey);
    configuration.setKey(entityProperty.getKey());
    configuration.setKeyConfigurations(keyConfigurations);
    return configuration;
  }
}
