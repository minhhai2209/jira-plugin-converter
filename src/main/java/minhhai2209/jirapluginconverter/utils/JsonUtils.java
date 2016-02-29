package minhhai2209.jirapluginconverter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class JsonUtils {

  private static final ObjectMapper om = getObjectMapper();

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return om.readValue(json, clazz);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  public static String toJson(Object o) {
    try {
      return om.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  public static <T> T fromJsonFile(File jsonFile, Class<T> clazz) {
    try {
      return om.readValue(jsonFile, clazz);
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }
}
