package generated_group_id.utils.jwt;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.core.reader.JwtClaimVerifiersBuilder;
import com.atlassian.jwt.core.reader.JwtIssuerSharedSecretService;
import com.atlassian.jwt.core.reader.JwtIssuerValidator;
import com.atlassian.jwt.core.reader.NimbusJwtReaderFactory;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import com.atlassian.jwt.reader.JwtReader;
import com.atlassian.jwt.reader.JwtReaderFactory;

import minhhai2209.jirapluginconverter.utils.ExceptionUtils;
import minhhai2209.jirapluginconverter.utils.JsonUtils;
import minhhai2209.jirapluginconverter.utils.LogFactory;

public class JwtVerifier {

  private static final Logger log = LogFactory.getLogger();

  private static final int JWT_REALM_LENGTH = "JWT ".length();

  public static JwtClaim read(
      String url, String authorization, String baseUrl, final String issuer, final String sharedSecret, String method) {

    try {

      log.info("Read and verify JWT {} {} {} {}", url, baseUrl, issuer, sharedSecret);

      JwtIssuerValidator jwtIssuerValidator = getJwtIssuerValidator(issuer);

      JwtIssuerSharedSecretService jwtIssuerSharedSecretService = getJwtIssuerSharedSecretService(sharedSecret);

      JwtReaderFactory jwtReaderFactory = new NimbusJwtReaderFactory(jwtIssuerValidator, jwtIssuerSharedSecretService);

      final String relativePath = JwtHelper.getRelativePath(url, baseUrl);

      Map<String, List<String>> parameters = getUrlParameters(url);

      final Map<String, String[]> parameterMap = JwtHelper.getParameterMap(parameters);

      Map<String, ? extends JwtClaimVerifier> jwtClaimVerifier = getJwtClaimVerifiers(relativePath, parameterMap, method);

      String jwtString = getJwtToken(authorization, parameters);
      if (jwtString == null) {
        return null;
      }

      JwtReader jwtReader = jwtReaderFactory.getReader(jwtString);
      Jwt jwt = jwtReader.readAndVerify(jwtString, jwtClaimVerifier);

      String claimJson = jwt.getJsonPayload();
      JwtClaim claim = JsonUtils.fromJson(claimJson, JwtClaim.class);

      return claim;

    } catch (JwtVerificationException e) {
      log.info("JWT is invalid", e);
      return null;
    } catch (Exception e) {
      log.error("Failed to read and verify JWT", e);
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  private static String getJwtToken(String authorization, Map<String, List<String>> parameters) {
    String jwtToken;
    List<String> jwtTokenParameter = parameters.get("jwt");
    if (jwtTokenParameter == null) {
      if (authorization == null) {
        return null;
      }
      jwtToken = authorization.substring(JWT_REALM_LENGTH);
    } else {
      jwtToken = jwtTokenParameter.get(0);
    }
    return jwtToken;
  }

  private static Map<String, ? extends JwtClaimVerifier> getJwtClaimVerifiers(
      final String relativePath, final Map<String, String[]> parameterMap, final String method)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    CanonicalHttpRequest canonicalHttpRequest = new CanonicalHttpRequest() {

      @Override
      public String getRelativePath() {
        return relativePath;
      }

      @Override
      public Map<String, String[]> getParameterMap() {
        return parameterMap;
      }

      @Override
      public String getMethod() {
        return method;
      }
    };
    Map<String, ? extends JwtClaimVerifier> jwtClaimVerifier = JwtClaimVerifiersBuilder.build(canonicalHttpRequest);
    return jwtClaimVerifier;
  }

  private static Map<String, List<String>> getUrlParameters(String url) throws URISyntaxException {
    List<NameValuePair> pairs = URLEncodedUtils.parse(new URI(url), "UTF-8");
    Map<String, List<String>> parameters = JwtHelper.getParameters(pairs);
    return parameters;
  }

  private static JwtIssuerSharedSecretService getJwtIssuerSharedSecretService(final String sharedSecret) {
    return new JwtIssuerSharedSecretService() {

      @Override
      public String getSharedSecret(String issuer) throws JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException {
        return sharedSecret;
      }
    };
  }

  private static JwtIssuerValidator getJwtIssuerValidator(final String issuer) {
    return new JwtIssuerValidator() {

      @Override
      public boolean isValid(String jwtIssuer) {
        return jwtIssuer.equals(issuer);
      }
    };
  }
}
