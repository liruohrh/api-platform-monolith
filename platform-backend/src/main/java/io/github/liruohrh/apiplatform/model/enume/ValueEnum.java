package io.github.liruohrh.apiplatform.model.enume;

import java.util.Objects;

public interface ValueEnum <T>{
  T getValue();
 default boolean is(T value){
    return Objects.equals(getValue(), value);
  }
}
