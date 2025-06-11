package io.github.liruohrh.apiplatform.common.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@Documented
@Constraint(validatedBy = { LoginParamCheckValidator.class})
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
@Target({ElementType.PARAMETER })
@Retention(RUNTIME)
public @interface LoginParamCheck {
  String message() default "";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };
}
