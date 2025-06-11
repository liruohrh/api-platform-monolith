package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apiplatform.apicommon.error.BusinessException;
import io.github.liruohrh.apiplatform.apicommon.error.ErrorCode;
import io.github.liruohrh.apiplatform.apicommon.error.ParamException;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.mapper.CommentMapper;
import io.github.liruohrh.apiplatform.mapper.HttpApiMapper;
import io.github.liruohrh.apiplatform.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.APIStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import io.github.liruohrh.apiplatform.model.req.api.APISortReq;
import io.github.liruohrh.apiplatform.model.req.api.ApiPurchasedSortReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import io.github.liruohrh.apiplatform.model.vo.ApiAndUsageVo;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author LYM
 * @description 针对表【http_api】的数据库操作Service实现
 * @createDate 2024-08-12 17:39:30
 */
@Slf4j
@Service
public class HttpApiServiceImpl extends ServiceImpl<HttpApiMapper, HttpApi>
    implements HttpApiService {

  private final ApiCallService apiCallService;
  @Resource
  private HttpApiMapper httpApiMapper;
  @Resource
  private CommentMapper commentMapper;

  public HttpApiServiceImpl(
      ApiCallService apiCallService
  ) {
    this.apiCallService = apiCallService;
  }


  @Override
  public void updateScore(Long id) {
    update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, id)
        .set(HttpApi::getScore, (int) (commentMapper.calculateScore(id) * 10))
    );
  }

  public PageResp<ApiAndUsageVo> purchaseAPIPage(
      int current,
      int size,
      ApiPurchasedSortReq sort,
      HttpApiResp search,
      Boolean isFree,
      Long userId
  ) {

    LambdaQueryWrapper<HttpApi> queryWrapper = getSearchQW(
        sort == null ? null : sort.toAPISortReq(),
        search,
        isFree
    );
    if (sort != null) {
      queryWrapper.orderBy(
          sort.getLeftTimes() != null,
          SortEnum.ASC == sort.getLeftTimes(),
          HttpApi::getPrice
      );
    }
    Page<ApiAndUsageVo> pageResult = httpApiMapper.listApiUsage(
        new Page<>(current, size),
        queryWrapper,
        userId
    );

    PageResp<ApiAndUsageVo> pageResp = new PageResp<>();
    pageResp.setData(pageResult.getRecords());
    pageResp.setPages(pageResult.getPages());
    pageResp.setTotal(pageResult.getTotal());
    pageResp.setCurrent(pageResult.getCurrent());
    pageResp.setSize(pageResult.getSize());
    return pageResp;
  }

  public <T> PageResp<T> page(
      int current,
      int size,
      APISortReq sort,
      HttpApiResp search,
      Boolean isFree,
      APIStatusEnum status,
      Function<HttpApi, T> converter
  ) {
    LambdaQueryWrapper<HttpApi> queryWrapper = getSearchQW(
        sort, search, isFree
    );
    if (status != null) {
      queryWrapper.eq(HttpApi::getStatus, status.getValue());
    }
    Page<HttpApi> pageResult = page(
        new Page<>(current, size),
        queryWrapper
    );
    PageResp<T> pageResp = new PageResp<>();
    pageResp.setData(pageResult.getRecords().stream()
        .map(converter).collect(Collectors.toList()));
    pageResp.setPages(pageResult.getPages());
    pageResp.setTotal(pageResult.getTotal());
    pageResp.setCurrent(pageResult.getCurrent());
    pageResp.setSize(pageResult.getSize());
    return pageResp;
  }

  private static LambdaQueryWrapper<HttpApi> getSearchQW(
      APISortReq sort,
      HttpApiResp search,
      Boolean isFree
  ) {
    LambdaQueryWrapper<HttpApi> queryWrapper = new LambdaQueryWrapper<>();
    if (Boolean.TRUE.equals(isFree)) {
      queryWrapper.eq(HttpApi::getPrice, 0.0);
    }

    if (search != null) {
      if (StrUtil.isNotEmpty(search.getName())) {
        queryWrapper.like(HttpApi::getName, search.getName());
        search.setName(null);
      }
      if (StrUtil.isNotEmpty(search.getDescription())) {
        queryWrapper.like(HttpApi::getDescription, search.getDescription());
        search.setDescription(null);
      }
      queryWrapper.setEntity(BeanUtil.copyProperties(search, HttpApi.class));
    }
    if (sort != null) {
      queryWrapper.orderBy(
          sort.getPrice() != null,
          SortEnum.ASC == sort.getPrice(),
          HttpApi::getPrice
      ).orderBy(
          sort.getOrderVolume() != null,
          SortEnum.ASC == sort.getOrderVolume(),
          HttpApi::getOrderVolume
      ).orderBy(
          sort.getScore() != null,
          SortEnum.ASC == sort.getScore(),
          HttpApi::getScore
      ).orderBy(
          sort.getCtime() != null,
          SortEnum.ASC == sort.getCtime(),
          HttpApi::getCtime
      ).orderBy(
          sort.getUtime() != null,
          SortEnum.ASC == sort.getUtime(),
          HttpApi::getUtime
      );
    }
    if (sort == null || sort.getCtime() == null) {
      queryWrapper = queryWrapper.orderByDesc(HttpApi::getCtime);
    }
    return queryWrapper;
  }


  @Override
  public Long addAPI(HttpApiAddReq req) {
    if (exists(null, req.getName())) {
      throw new BusinessException(Resp.fail(ErrorCode.ALREADY_EXISTS, "already has this API name"));
    }

    HttpApi newValue = BeanUtil.copyProperties(req, HttpApi.class);
    if (newValue.getPrice().equals(0.0)) {
      newValue.setFreeTimes(0);
    }
    newValue.setPrice(new BigDecimal(Double.toString(req.getPrice()))
        .setScale(5, RoundingMode.HALF_UP).doubleValue());
    newValue.setOwnerId(LoginUserHolder.getUserId());
    newValue.setStatus(APIStatusEnum.ROLL_OFF.getValue());
    save(newValue);
    return newValue.getId();
  }

  @Override
  public void launchAPI(Long apiId) {
    validUpdate(apiId, LoginUserHolder.get());

    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, apiId)
        .set(HttpApi::getStatus, APIStatusEnum.LAUNCH.getValue())
    ));
  }

  @Override
  public void rollOffAPI(Long apiId) {
    validUpdate(apiId, LoginUserHolder.get());

    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, apiId)
        .set(HttpApi::getStatus, APIStatusEnum.ROLL_OFF.getValue())
    ));
  }

  @Override
  public void updateAPI(HttpApiUpdateReq req) {
    User loginUser = LoginUserHolder.get();
    HttpApi updateValue = BeanUtil.copyProperties(req, HttpApi.class);

    HttpApi httpApi = validUpdate(updateValue.getId(), loginUser);
    if (updateValue.getPrice() != null) {
      if(updateValue.getPrice().equals(0.0)){
        updateValue.setFreeTimes(0);
      }else{
        httpApi.setPrice(new BigDecimal(Double.toString(updateValue.getPrice()))
            .setScale(5, RoundingMode.HALF_UP).doubleValue());
      }
    }
    if(httpApi.getStatus() != null && !APIStatusEnum.contains(httpApi.getStatus())){
        throw new ParamException("非法API状态");
    }
    if (StrUtil.isNotEmpty(updateValue.getName())) {
      if (updateValue.getName().equals(httpApi.getName())) {
        updateValue.setName(null);
      } else if (exists(null, updateValue.getName())) {
        throw new BusinessException(
            Resp.fail(ErrorCode.ALREADY_EXISTS, "already has this API name"));
      }
    }
    MustUtils.dbSuccess(updateById(updateValue));
  }

  private boolean exists(Long id, String name) {
    LambdaQueryWrapper<HttpApi> queryWrapper = new LambdaQueryWrapper<HttpApi>()
        .select(HttpApi::getId);
    if (id != null) {
      return getOne(queryWrapper
          .eq(HttpApi::getId, id)) != null;
    } else if (name != null) {
      return getOne(queryWrapper
          .eq(HttpApi::getName, name)) != null;
    }
    return false;
  }

  /**
   * 存在，且操作者必须是用户或者管理员
   */
  private HttpApi validUpdate(Long updateHttpApi, User loginUser) {
    HttpApi httpApi = getById(updateHttpApi);
    if (httpApi == null) {
      throw new ParamException(Resp.fail(ErrorCode.NOT_EXISTS, "API不存在"));
    }
    if (!loginUser.getId().equals(httpApi.getOwnerId()) && !RoleEnum.ADMIN.eq(
        loginUser.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    return httpApi;
  }

  @Override
  public void deleteAPI(Long apiId) {
    User loginUser = LoginUserHolder.get();

    validUpdate(apiId, loginUser);

    MustUtils.dbSuccess(removeById(apiId));
  }

}




