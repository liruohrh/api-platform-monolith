package io.github.liruohrh.apiplatform.model.enume;


import java.util.Arrays;
import java.util.List;

public enum OrderStatusEnum implements ValueEnum<Integer>{
  WAIT_PAY(0),
  PAID(1),
  CANCEL(2),
  REFUNDING(40),
  REFUND_SUCCESS(41),
  REFUND_FAIL(42),
  REFUND_CANCEL(43),
  ;
  private final int value;

  OrderStatusEnum(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }

  public static List<Integer> effect(){
    return Arrays.asList(
        OrderStatusEnum.PAID.getValue(),
        OrderStatusEnum.REFUNDING.getValue(),
        OrderStatusEnum.REFUND_CANCEL.getValue(),
        OrderStatusEnum.REFUND_FAIL.getValue()
    );
  }
  public static boolean isEffect(int value){
    return  OrderStatusEnum.PAID.is(value)
        ||   OrderStatusEnum.REFUNDING.is(value)
        ||   OrderStatusEnum.REFUND_CANCEL.is(value)
        ||   OrderStatusEnum.REFUND_FAIL.is(value);
  }
}
