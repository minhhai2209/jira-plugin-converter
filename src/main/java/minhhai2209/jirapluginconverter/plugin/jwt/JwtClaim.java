package minhhai2209.jirapluginconverter.plugin.jwt;

public class JwtClaim {

  private String iss;

  private JwtContext context;

  public String getIss() {
    return iss;
  }

  public void setIss(String iss) {
    this.iss = iss;
  }

  public JwtContext getContext() {
    return context;
  }

  public void setContext(JwtContext context) {
    this.context = context;
  }
}
