package io.github.liruohrh.apiplatform.common.servlet;

import io.github.liruohrh.apiplatform.common.holder.RequestHolder;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class HolderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      RequestHolder.set(request);
      RequestHolder.setResp(response);
      filterChain.doFilter(request, response);
    }finally {
      RequestHolder.clear();
    }
  }
}
