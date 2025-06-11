package io.github.liruohrh.apiplatform.apicommon.utils;

import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ValidUtils {

  public static final Pattern INTEGER = Pattern.compile("^\\d+$");
  public static final Pattern PHONE_NUMBER = Pattern.compile("^(?:(?:\\+|00)86)?1[3-9]\\d{9}$");
  public static final Predicate<String> isPhoneNumber = PHONE_NUMBER.asPredicate();
  public static final Pattern DOMAIN = Pattern.compile(
      "^[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$");

  /**
   * @param email 不校验用户名部分，只允许域名而不允许ip
   */
  public static boolean isEmail(String email) {
    String[] split = email.split("@");
    if (split.length != 2) {
      return false;
    }
    return isDomain(split[1]);
  }

  /**
   *
   * @param domain 仅校验字母、数字、-
   */
  public static boolean isDomain(String domain) {
    return DOMAIN.matcher(domain).matches() && !INTEGER.asPredicate().test(domain.substring(domain.lastIndexOf(".") + 1));
  }

  public static boolean isDomainAddr(String domainAddr) {
    String domain = domainAddr;
    int portI = domainAddr.lastIndexOf(":");
    if (portI != -1) {
      domain = domain.substring(0, portI);
      if (portI + 1 > domainAddr.length() - 1) {
        return false;
      }
      if (!isPort(domainAddr.substring(portI + 1))) {
        return false;
      }
    }
    return isDomain(domain);
  }

  public static boolean isPort(String port) {
    try {
      return isPort(Integer.parseInt(port));
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static boolean isPort(int port) {
    return port > 0 && port < 65536;
  }

  public static boolean isIpAddr(String ipAddr) {
    String[] split = ipAddr.split("\\.|:");
    if (split.length != 4 && split.length != 5) {
      return false;
    }
    try {
      for (int i = 0; i < 4; i++) {
        if (Integer.parseInt(split[i]) > 255) {
          return false;
        }
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return split.length != 5 || isPort(split[4]);
  }

  public static boolean isSocketAddr(String addr) {
    return isDomainAddr(addr) || isIpAddr(addr);
  }

  public static boolean isUrl(String url) {
    String pureUrl = url.trim();
    String socketAddr;
    if (pureUrl.startsWith("http://")) {
      socketAddr = pureUrl.substring("http://".length());
    } else if (pureUrl.startsWith("https://")) {
      socketAddr = pureUrl.substring("https://".length());
    } else {
      return false;
    }
    if (StringUtils.isEmpty(socketAddr)) {
      return false;
    }
    int firstSlashI = socketAddr.indexOf("/");
    if (firstSlashI != -1) {
      socketAddr = socketAddr.substring(0, firstSlashI);
    }
    return isSocketAddr(socketAddr);
  }

  public static void port(Integer port, String error) {
    if (port != null && !isPort(port)) {
      throw new ParamException(error);
    }
  }

  public static void phoneNumber(String phone) {
    if (StringUtils.isNotBlank(phone) && !isPhoneNumber.test(phone)) {
      throw new ParamException("手机号不正确");
    }
  }

  public static void email(String email) {
    if (StringUtils.isNotBlank(email) && !isEmail(email)) {
      throw new ParamException("邮箱号不正确");
    }
  }

  public static void url(String url, String error) {
    if (StringUtils.isNotBlank(url) && !isUrl(url)) {
      throw new ParamException(error);
    }
  }
}
