package minhhai2209.jirapluginconverter.utils;

public class ExceptionUtils {

  public static void throwUnchecked(Exception e) {
    throw new IllegalStateException(e);
  }
}
