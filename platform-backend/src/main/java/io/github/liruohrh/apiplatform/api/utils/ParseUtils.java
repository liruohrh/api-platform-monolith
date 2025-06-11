package io.github.liruohrh.apiplatform.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {
  public static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+(.[0-9]+)?");
  public static Double parseTemp(String str) {
    try {
      return Double.parseDouble(str.replace("°C", ""));
    } catch (NumberFormatException e) {
      Matcher matcher = NUMBER_PATTERN.matcher(str);
      if(!matcher.find()){
        return null;
      }
      return Double.parseDouble(matcher.group(0));
    }
  }
}
