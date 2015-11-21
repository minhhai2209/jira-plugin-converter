package minhhai2209.jirapluginconverter.connect.descriptor.condition;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=Include.NON_NULL)
public class ConditionWrapper extends Condition {

  private List<Condition> or;
  private List<Condition> and;
  
  public List<Condition> getOr() {
    return or;
  }
  public void setOr(List<Condition> or) {
    this.or = or;
  }
  public List<Condition> getAnd() {
    return and;
  }
  public void setAnd(List<Condition> and) {
    this.and = and;
  }
}
