package io.github.liruohrh.apiplatform.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface HttpAPICallService {
  ResponseEntity<byte[]> debugCallAPI(Long apiId, HttpServletRequest req, HttpServletResponse resp);

  void afterCallAPI(boolean isSuccess, int timeConsumingMs, Boolean isFreeAPI, Long apiId, Long callerId);

  void checkLeftTimes(Long apiId, Long callerId);
}
