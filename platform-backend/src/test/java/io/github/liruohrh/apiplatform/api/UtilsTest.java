package io.github.liruohrh.apiplatform.api;

import io.github.liruohrh.apiplatform.api.utils.ParseUtils;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testParseTemp() {
    Assertions.assertEquals(12.4, ParseUtils.parseTemp("12.4 °C"));
    Matcher matcher = ParseUtils.NUMBER_PATTERN.matcher("12.4 °C");
    Assertions.assertTrue( matcher.find());
    Assertions.assertEquals("12.4", matcher.group());
  }
}
