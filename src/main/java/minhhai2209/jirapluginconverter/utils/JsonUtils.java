package minhhai2209.jirapluginconverter.utils;

import java.io.File;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private static final ObjectMapper om = new ObjectMapper();

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
