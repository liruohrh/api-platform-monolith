package io.github.liruohrh.apiplatform.config;

import io.github.liruohrh.apiplatform.common.servlet.ApiCallFilter;
import io.github.liruohrh.apiplatform.common.servlet.HolderFilter;
import io.github.liruohrh.apiplatform.common.servlet.LoginFilter;
import io.github.liruohrh.apiplatform.common.servlet.SinglePageHistoryModeRedirectFilter;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.controller.OssController;
import io.github.liruohrh.apiplatform.rpc.service.RpcHttpApiService;
import io.github.liruohrh.apiplatform.rpc.service.RpcUserService;
import io.github.liruohrh.apiplatform.service.UserService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.PathResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
//  @Bean
//  public TomcatContextCustomizer sameSiteCookiesConfig() {
//    return context -> {
//      final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
//      cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
//      context.setCookieProcessor(cookieProcessor);
//    };
//  }

  @Bean
  public WebMvcConfigurer webMvcConfigurer(){
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:9000",
                "http://127.0.0.1:9000",
                "http://localhost:8000",
                "http://127.0.0.1:8000",
                "http://localhost",
                "http://127.0.0.1"
            )
            .allowedMethods("GET", "POST", "DELETE", "PUT")
            .allowCredentials(true)
            .allowedHeaders("*")
            .exposedHeaders("*")
            .maxAge(3 * 60 * 10);
      }

      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(CommonConstant.OSS_LOCAL_URL_PATH_PREFIX + "**")
            .addResourceLocations(new PathResource(OssController.resolveInRootDir(null)));
      }
    };
  }
  @Bean
  public FilterRegistrationBean<LoginFilter> loginFilter(
      APIPlatformProperties apiPlatformProperties,
      UserService userService,
      CacheManager cacheManager
  ){
    FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new LoginFilter(
        apiPlatformProperties.getLogin().getWhiteList(),
        userService
    ));
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 500);
    return registrationBean;
  }
  @Bean
  public FilterRegistrationBean<HolderFilter> holderFilterFilter(){
    FilterRegistrationBean<HolderFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new HolderFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
    return registrationBean;
  }
  @Bean
  public FilterRegistrationBean<SinglePageHistoryModeRedirectFilter> singlePageHistoryModeRedirectFilter(){
    FilterRegistrationBean<SinglePageHistoryModeRedirectFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new SinglePageHistoryModeRedirectFilter());
    registrationBean.addUrlPatterns("/pages/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }
  @Bean
  public FilterRegistrationBean<ApiCallFilter> apiCallFilter(
      RpcUserService rpcUserService,
      RpcHttpApiService rpcHttpApiService,
      ApiGatewayProperties apiGatewayProperties,
      CacheManager cacheManager
  ){
    FilterRegistrationBean<ApiCallFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new ApiCallFilter(
        rpcUserService, rpcHttpApiService,
        apiGatewayProperties, cacheManager
    ));
    registrationBean.addUrlPatterns("/api/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
    return registrationBean;
  }
}
