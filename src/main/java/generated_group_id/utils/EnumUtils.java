package generated_group_id.utils;

public class EnumUtils {

  public static boolean equals(Enum<?> left, Enum<?> right) {
    return left.name().equalsIgnoreCase(right.name());
  }
}
