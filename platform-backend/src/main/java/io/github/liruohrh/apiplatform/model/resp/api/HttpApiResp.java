package io.github.liruohrh.apiplatform.model.resp.api;

import java.io.Serializable;
import lombok.Data;

@Data
public class HttpApiResp implements Serializable {
  private Long id;
  private Long ownerId;

  /**
   *
   */
  private String description;


  /**
   *
   */
  private String name;
  private String logoUrl;

  /**
   * 大写
   */
  private String method;

  private String protocol;
  private String domain;

  private String path;

  /**
   *
   */
  private String params;

  /**
   *
   */
  private String reqHeaders;

  /**
   *
   */
  private String reqBody;

  /**
   *
   */
  private String respHeaders;

  /**
   *
   */
  private String respBody;

  private String respSuccess;
  private String respFail;
  private String errorCodes;

  /**
   * 最多3位小数
   */
  private Double price;

  /**
   *
   */
  private Integer freeTimes;

  /**
   *
   */
  private Integer orderVolume;

  /**
   *
   */
  private Integer score;

  /**
   * 0-待审核，1-上线，2-下线，3-被禁用
   */
  private Integer status;
  private Long ctime;
  private Long utime;

  private static final long serialVersionUID = 1L;

  public static HttpApiResp of(String search) {
    HttpApiResp httpApiResp = new HttpApiResp();
    httpApiResp.setName(search);
    httpApiResp.setDescription(search);
    return httpApiResp;
  }
}
