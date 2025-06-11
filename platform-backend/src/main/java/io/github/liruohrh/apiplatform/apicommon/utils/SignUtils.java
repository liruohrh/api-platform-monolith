package io.github.liruohrh.apiplatform.apicommon.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SignUtils {
  public static final String KEY_APP_KEY = "appKey";
  public static final String KEY_NONCE= "nonce";
  public static final String KEY_TIMESTAMP = "timestamp";
  public static final String KEY_EXTRA = "extra";
  public static final String KEY_SIGN = "sign";
  public static boolean verify(
      String appKey,
      String appSecret,
      String nonce,
      String timestamp,
      String extra,
      String sign
  ) {
    return generateSign(appKey, appSecret, nonce, timestamp, extra).equals(sign);
  }

  public static String generateSign(
      String appKey,
      String appSecret,
      String nonce,
      String timestamp,
      String extra
  ) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("md5");
      byte[] digest = md5.digest(
          (appKey + appSecret + nonce + timestamp + extra).getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
