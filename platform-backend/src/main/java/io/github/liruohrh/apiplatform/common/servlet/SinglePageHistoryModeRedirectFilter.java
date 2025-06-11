package io.github.liruohrh.apiplatform.common.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 把前端页面路由重定向到index.html
 */
public class SinglePageHistoryModeRedirectFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    request.getRequestDispatcher("/static/index.html").forward(request, response);
  }
}
