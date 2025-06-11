package io.github.liruohrh.apiplatform.webcommon.exception;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Component
@WebFilter(urlPatterns = "/*")
public class GlobalExceptionFilter extends OncePerRequestFilter implements Ordered {
  @Resource
  private List<HandlerExceptionResolver> handlerExceptionResolverComposite;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    }catch (Exception e){
      for (HandlerExceptionResolver handlerExceptionResolver : handlerExceptionResolverComposite) {
        ModelAndView modelAndView = handlerExceptionResolver.resolveException(request, response,
            null, e);
        if (modelAndView != null) {
          return;
        }
      }
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
