package generated_group_id.utils;

import java.util.Locale;

import com.atlassian.sal.api.message.LocaleResolver;

public class LocaleUtils {

  public static String getLocale(LocaleResolver localeResolver) {
    Locale locale = localeResolver.getLocale();
    StringBuilder buf = new StringBuilder();

    if (locale.getLanguage().length() > 0) {
      buf.append(locale.getLanguage());
    }

    if (locale.getCountry().length() > 0) {
      buf.append("-");
      buf.append(locale.getCountry());
    }
    return buf.toString();
  }
}
