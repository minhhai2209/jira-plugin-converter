package minhhai2209.jirapluginconverter.plugin.condition;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.fasterxml.jackson.databind.ObjectMapper;
import minhhai2209.jirapluginconverter.plugin.render.ParameterContextBuilder;
import minhhai2209.jirapluginconverter.plugin.utils.HttpClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.util.Map;

public class RemoteCondition implements Condition {
  
  private Map<String, String> params;
  private String conditionUrl;
  private ObjectMapper om = new ObjectMapper();

  @Override
  public void init(Map<String, String> params) throws PluginParseException {
    this.params = params;
    this.conditionUrl = params.get("condition");
    
  }

  @Override
  public boolean shouldDisplay(Map<String, Object> context) {
    return conditionUrl == null || getRemoteCondition(context);
  }
  
  private boolean getRemoteCondition(Map<String, Object> context) {
    try {
      Map<String, String> productContext = ParameterContextBuilder.buildContext(null, context, null);
      conditionUrl = ParameterContextBuilder.buildUrl(conditionUrl, productContext);

      HttpClient client = HttpClientFactory.build();
      URIBuilder builder = new URIBuilder(conditionUrl);
      if (params != null) {
        for (String key : params.keySet()) {
          builder.addParameter(key, params.get(key));
        }
      }
      HttpGet httpGet = new HttpGet(builder.build());
      HttpResponse response = client.execute(httpGet);
      DisplayDto displayDto = om.readValue(response.getEntity().getContent(), DisplayDto.class);
      return displayDto.isShouldDisplay();
    } catch (Exception e) {
      //any exception will return false
    }
    
    return false;
  }

}
