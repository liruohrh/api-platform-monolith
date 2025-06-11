package io.github.liruohrh.apiplatform.rpc.service;


import io.github.liruohrh.apiplatform.model.entity.User;

public interface RpcUserService {
  User getUserByAppKey(String appKey);
}
