package minhhai2209.jirapluginconverter.plugin.utils;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;

import minhhai2209.jirapluginconverter.utils.ExceptionUtils;

public class HttpClientFactory {

  // private static final Logger log = LogFactory.getLogger();

  public static HttpClient build() {
    try {
      // log.debug("Build HttpClient");
      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
      SSLConnectionSocketFactory sslSocketFactory = getSslSocketFactory();
      // avoid caching resolved IPs
      NoConnectionReuseStrategy connectionReusesStrategy = new NoConnectionReuseStrategy();
      httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
      httpClientBuilder.setConnectionReuseStrategy(connectionReusesStrategy);
      return httpClientBuilder.build();
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  private static SSLConnectionSocketFactory getSslSocketFactory() {
    SSLContext sslContext = getSslContext();
    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    return sslSocketFactory;
  }

  private static SSLContext getSslContext() {
    try {
      SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      TrustStrategy trustStrategy = new TrustAllStrategy();
      sslContextBuilder.loadTrustMaterial(keyStore, trustStrategy);
      SSLContext sslContext = sslContextBuilder.build();
      return sslContext;
    } catch (Exception e) {
      ExceptionUtils.throwUnchecked(e);
    }
    return null;
  }

  private static class TrustAllStrategy implements TrustStrategy {

    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      return true;
    }
  }
}
