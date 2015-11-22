package minhhai2209.jirapluginconverter.plugin.jwt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;

public class JwtHelper {

  public static String getRelativePath(String url, String baseUrl) {
    String path = url.split("\\?")[0];
    int baseUrlLength = baseUrl.length();
    String relativePath = path.substring(baseUrlLength);
    if (relativePath.isEmpty()) {
      relativePath = "/";
    }
    return relativePath;
  }

  public static Map<String, String[]> getParameterMap(Map<String, List<String>> parameters) {
    final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
    for (Entry<String, List<String>> parameter : parameters.entrySet()) {
      String parameterName = parameter.getKey();
      if (!parameterName.equals("jwt")) {
        List<String> parameterValues = parameter.getValue();
        String[] values = parameterValues.toArray(new String[] {});
        parameterMap.put(parameterName, values);
      }
    }
    return parameterMap;
  }

  public static  Map<String, List<String>> getParameters(List<NameValuePair> pairs) {
    Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    if (pairs != null) {
      for (NameValuePair pair : pairs) {
        String pairName = pair.getName();
        String pairValue = pair.getValue();
        List<String> parameterValues = parameters.get(pairName);
        if (parameterValues == null) {
          parameterValues = new ArrayList<String>();
          parameters.put(pairName, parameterValues);
        }
        parameterValues.add(pairValue);
      }
    }
    return parameters;
  }
}
