package io.github.liruohrh.apiplatform.webcommon.exception;

import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author:liruo
 * @Date:2023-06-19-12:25:52
 * @Desc
 * - sendErrror
 * - 服务器异常
 * - Filter异常
 * - Servlet的异常（除了Spring的DispatchServlet）
 */
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
@Slf4j
public class MErrorController implements ErrorController {


  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resp<Void>> error(HttpServletRequest request) {
    Resp<Void> baseResp;
    HttpStatus status = this.getStatus(request);
    Throwable exception = this.getException(request);
    String message = this.getMessage(request);
    if (exception == null) {
      //StandardHostValve.status
      //message maybe not the standard http message. response.sendError(code[, reason])
      if(message == null || message.isEmpty()){
        baseResp = Resp.fail(ErrorCode.SYSTEM, status.getReasonPhrase());
      }else{
        baseResp = Resp.fail(ErrorCode.SYSTEM,message);
      }
    } else {
      //StandardHostValve.throwable
      baseResp = Resp.fail(ErrorCode.SYSTEM);
    }

    log.warn("ErrorController: requestUri={}, status={}, message={}",
        this.getRequestUri(request), status, message, exception);
    return new ResponseEntity<>(baseResp, status);
  }

  private HttpStatus getStatus(HttpServletRequest req) {
    return HttpStatus.resolve((int) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
  }

  private String getMessage(HttpServletRequest req) {
    return (String) req.getAttribute(RequestDispatcher.ERROR_MESSAGE);
  }

  private Throwable getException(HttpServletRequest req) {
    return (Throwable) req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
  }

  private String getRequestUri(HttpServletRequest req) {
    return (String) req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
  }
}
