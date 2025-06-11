package io.github.liruohrh.apiplatform.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import java.time.Instant;

public class SecurityUtils {
  public static String generateAppSecret(String key){
    return  DigestUtil.md5Hex(
        CommonConstant.SALT +
            key +
        RandomUtil.randomString(CommonConstant.APP_SECRET_RANDOM_LEN)
         + Instant.now().toEpochMilli()
    );
  }
  public static String generateAppKey(String key){
    return  DigestUtil.md5Hex(
        CommonConstant.SALT
            + key
            + RandomUtil.randomString(CommonConstant.APP_KEY_RANDOM_LEN)
            + Instant.now().toEpochMilli()
    );
  }
  public static String digestPasswd(String passwd){
    return  DigestUtil.md5Hex(CommonConstant.SALT + passwd);
  }
}
