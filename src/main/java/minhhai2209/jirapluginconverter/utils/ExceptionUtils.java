package minhhai2209.jirapluginconverter.utils;

import com.google.common.base.Throwables;

public class ExceptionUtils {

  public static void throwUnchecked(Exception e) {
    throw new IllegalStateException(e);
  }

  public static String getStackTrace(Exception e) {
    return Throwables.getStackTraceAsString(e);
  }
}
