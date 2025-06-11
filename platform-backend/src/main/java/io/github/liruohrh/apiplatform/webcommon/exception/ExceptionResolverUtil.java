package io.github.liruohrh.apiplatform.webcommon.exception;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @Author:liruo
 * @Date:2023-06-26-14:22:31
 * @Desc
 */
public class ExceptionResolverUtil {

  /**
   * @see org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver
   * @param ex it message maybe not the standard http message.
   *           maybe throw {@link ResponseStatusException}
   *           or use {@link org.springframework.web.bind.annotation.ResponseStatus} on exception
   */
  public static void handlerResponseStatusException(HttpServletRequest request, HttpServletResponse response, ResponseStatusException ex) {
    ex.getResponseHeaders().forEach((name, values) -> {
      values.forEach((value) -> {
        response.addHeader(name, value);
      });
    });
  }

  /**
   * @see org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
   */
  public static HttpStatus handlerServletException(HttpServletRequest request, HttpServletResponse response, Exception ex){
    if (ex instanceof HttpRequestMethodNotSupportedException) {
      String[] supportedMethods =((HttpRequestMethodNotSupportedException) ex).getSupportedMethods();
      if (supportedMethods != null) {
        response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
      }
      return HttpStatus.METHOD_NOT_ALLOWED;
    }

    if (ex instanceof HttpMediaTypeNotSupportedException) {
      List<MediaType> mediaTypes = ((HttpMediaTypeNotSupportedException)ex).getSupportedMediaTypes();
      if (!CollectionUtils.isEmpty(mediaTypes)) {
        response.setHeader("Accept", MediaType.toString(mediaTypes));
        if (request.getMethod().equals("PATCH")) {
          response.setHeader("Accept-Patch", MediaType.toString(mediaTypes));
        }
      }
      return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    }

    if (ex instanceof HttpMediaTypeNotAcceptableException) {
      return HttpStatus.NOT_ACCEPTABLE;
    }

    if (ex instanceof MissingPathVariableException) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    if (ex instanceof MissingServletRequestParameterException) {
      return HttpStatus.BAD_REQUEST;
    }

    if (ex instanceof ServletRequestBindingException) {
      return HttpStatus.BAD_REQUEST;
    }

    if (ex instanceof ConversionNotSupportedException) {
      return HttpStatus.INTERNAL_SERVER_ERROR;    }

    if (ex instanceof TypeMismatchException) {
      return HttpStatus.BAD_REQUEST;    }

    if (ex instanceof HttpMessageNotReadableException) {
      return HttpStatus.BAD_REQUEST;    }

    if (ex instanceof HttpMessageNotWritableException) {
      return HttpStatus.INTERNAL_SERVER_ERROR;     }


    if (ex instanceof MissingServletRequestPartException) {
      return HttpStatus.BAD_REQUEST;      }


    if (ex instanceof NoHandlerFoundException) {
      return HttpStatus.NOT_FOUND;      }

    if (ex instanceof AsyncRequestTimeoutException) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
    if (ex instanceof HttpSessionRequiredException) {
      return HttpStatus.BAD_REQUEST;
    }

    return null;
  }
}
