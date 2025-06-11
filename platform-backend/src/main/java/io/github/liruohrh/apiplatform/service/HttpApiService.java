package io.github.liruohrh.apiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.enume.APIStatusEnum;
import io.github.liruohrh.apiplatform.model.req.api.APISortReq;
import io.github.liruohrh.apiplatform.model.req.api.ApiPurchasedSortReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import io.github.liruohrh.apiplatform.model.vo.ApiAndUsageVo;
import java.util.function.Function;

/**
 * @author LYM
 * @description 针对表【http_api】的数据库操作Service
 * @createDate 2024-08-12 17:39:30
 */
public interface HttpApiService extends IService<HttpApi> {
  void updateScore(Long id);

   PageResp<ApiAndUsageVo> purchaseAPIPage(
      int current,
      int size,
      ApiPurchasedSortReq sort,
      HttpApiResp search,
      Boolean isFree,
      Long purchasedAPIUserId
  );
  <T> PageResp<T> page(
      int current,
      int size,
      APISortReq sort,
      HttpApiResp search,
      Boolean isFree,
      APIStatusEnum status,
      Function<HttpApi, T> converter
  );
  Long addAPI(HttpApiAddReq httpApiAddReq);

  void launchAPI(Long apiId);

  void rollOffAPI(Long apiId);

  void updateAPI(HttpApiUpdateReq httpApiUpdateReq);

  void deleteAPI(Long apiId);
}
