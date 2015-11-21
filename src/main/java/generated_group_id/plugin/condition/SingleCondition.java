package generated_group_id.plugin.condition;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.fasterxml.jackson.databind.ObjectMapper;

import generated_group_id.utils.http.HttpClientFactory;

public class SingleCondition implements Condition {
  
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
    return conditionUrl == null || getRemoteCondition();
  }
  
  private boolean getRemoteCondition() {
    try {
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
