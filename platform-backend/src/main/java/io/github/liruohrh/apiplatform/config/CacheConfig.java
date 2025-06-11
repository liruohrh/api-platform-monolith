package io.github.liruohrh.apiplatform.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.liruohrh.apiplatform.constant.RedisConstant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
  @Bean
  public CacheManager caffeineCacheManager() {
    Map<String, String> cacheConfigs = new HashMap<>();
    cacheConfigs.put(RedisConstant.PREFIX_EMAIL_CAPTCHA, "expireAfterWrite=5m");
    cacheConfigs.put(RedisConstant.PREFIX_EMAIL_RETRY, "expireAfterWrite=5m");
    cacheConfigs.put(RedisConstant.PREFIX_EMAIL_FREEZE, "expireAfterWrite=5h");

    cacheConfigs.put(RedisConstant.PREFIX_EMAIL_VERIFY_CAPTCHA_RETRY, "expireAfterWrite=5m");
    cacheConfigs.put(RedisConstant.PREFIX_EMAIL_VERIFY_FREEZE, "expireAfterWrite=5h");

    cacheConfigs.put(RedisConstant.PREFIX_API_CALL_REPLAY_ATTACK, "expireAfterWrite=5m");
    cacheConfigs.put(RedisConstant.PREFIX_LOGIN, "expireAfterWrite=12h");
    return new CaffeineCacheManager(){
      @Override
      protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(
          String name) {
        return Caffeine.from(cacheConfigs.get(name)).build();
      }
    };
  }
}
