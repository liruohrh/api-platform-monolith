package io.github.liruohrh.apiplatform.common.validator;

import io.github.liruohrh.apiplatform.common.util.ValidatorUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FloatFractionalValidator implements ConstraintValidator<FloatFractional, Number> {

  private FloatFractional floatFractional;

  @Override
  public void initialize(FloatFractional floatFractional) {
    this.floatFractional = floatFractional;
  }

  @Override
  public boolean isValid(Number value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    if (!(value instanceof Double) && !(value instanceof Float)) {
      throw new IllegalArgumentException(
          "invalid arg type, " + FloatFractional.class.getSimpleName()
              + " require float or double");
    }
    String str = value.toString();
    int dotIndex = str.lastIndexOf(".");
    if (dotIndex == -1) {
      return true;
    }

    int length = str.length() - dotIndex;
    if (length < floatFractional.min() || length > floatFractional.max()) {
      String msg = floatFractional.message();
      if (msg != null && !msg.isEmpty()) {
        msg = ", " + msg;
      }else{
        msg = "";
      }
      ValidatorUtils.addErrorMessage(
          context,
          "float's fractional must in ["
              + floatFractional.min()
              + ","
              + floatFractional.max()
              + "]" + msg
      );
      return false;
    }
    return true;
  }
}
