package minhhai2209.jirapluginconverter.plugin.jwt;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.core.writer.JwtClaimsBuilder;
import com.atlassian.jwt.core.writer.NimbusJwtWriterFactory;
import com.atlassian.jwt.httpclient.CanonicalHttpUriRequest;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.jwt.writer.JwtWriter;
import com.atlassian.jwt.writer.JwtWriterFactory;

import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class JwtComposer {

  public static String compose(
      String key,
      String sharedSecret,
      String method,
      String apiPath,
      List<NameValuePair> pairs,
      JwtContext context) {

    try {
      long issuedAt = System.currentTimeMillis() / 1000L;
      long expiresAt = issuedAt + 180L;
      JwtJsonBuilder jwtJsonBuilder = new JsonSmartJwtJsonBuilder()
              .issuedAt(issuedAt)
              .expirationTime(expiresAt)
              .issuer(key)
              .claim("context", context);
      Map<String, List<String>> parameters = JwtHelper.getParameters(pairs);
      Map<String, String[]> parameterMap = JwtHelper.getParameterMap(parameters);
      CanonicalHttpUriRequest canonicalHttpUrlRequest = new CanonicalHttpUriRequest(method, apiPath, null, parameterMap);
      JwtClaimsBuilder.appendHttpRequestClaims(jwtJsonBuilder, canonicalHttpUrlRequest);
      JwtWriterFactory jwtWriterFactory = new NimbusJwtWriterFactory();
      String unsignedJwt = jwtJsonBuilder.build();
      JwtWriter macSigningWriter = jwtWriterFactory.macSigningWriter(SigningAlgorithm.HS256, sharedSecret);
      String jwt = macSigningWriter.jsonToJwt(unsignedJwt);
      return jwt;
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return apiPath;
  }
}
