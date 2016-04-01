package minhhai2209.jirapluginconverter.plugin.jwt;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.core.reader.JwtClaimVerifiersBuilder;
import com.atlassian.jwt.core.reader.JwtIssuerSharedSecretService;
import com.atlassian.jwt.core.reader.JwtIssuerValidator;
import com.atlassian.jwt.core.reader.NimbusJwtReaderFactory;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import com.atlassian.jwt.reader.JwtReader;
import com.atlassian.jwt.reader.JwtReaderFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class JwtVerifier {

  public static boolean verify(
    String relativeURI, String jwtString, Map<String, String[]> parameterMap, final String issuer, final String sharedSecret, String method) {

    try {

      JwtIssuerValidator jwtIssuerValidator = getJwtIssuerValidator(issuer);

      JwtIssuerSharedSecretService jwtIssuerSharedSecretService = getJwtIssuerSharedSecretService(sharedSecret);

      JwtReaderFactory jwtReaderFactory = new NimbusJwtReaderFactory(jwtIssuerValidator, jwtIssuerSharedSecretService);

      Map<String, ? extends JwtClaimVerifier> jwtClaimVerifier = getJwtClaimVerifiers(relativeURI, parameterMap, method);

      JwtReader jwtReader = jwtReaderFactory.getReader(jwtString);
      jwtReader.readAndVerify(jwtString, jwtClaimVerifier);
    } catch (Exception e) {
      System.out.println("JWT VERIFY ERROR: " + e.getMessage());
      return false;
    }
    return true;
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
