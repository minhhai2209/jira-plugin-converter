package minhhai2209.jirapluginconverter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFactory {

  private static final Logger log = getLogger();

  /**
   * Get a logger for the caller class.
   *
   * @return
   */
  public static Logger getLogger() {
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    /*
     * stackTrace[0] is for Thread.currentThread().getStackTrace() stackTrace[1] is for this method log()
     */
    String className = stackTrace[2].getClassName();
    if (log != null) {
      log.trace("Get logger for class {}", className);
    }
    return LoggerFactory.getLogger(className);
  }

}