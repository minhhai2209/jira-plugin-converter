package minhhai2209.jirapluginconverter.plugin.utils;

public class EnumUtils {

  public static boolean equals(Enum<?> left, Enum<?> right) {
    return left.name().equalsIgnoreCase(right.name());
  }
}
