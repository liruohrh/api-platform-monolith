package io.github.liruohrh.apiplatform.constant;

public interface CommonConstant {
    String SALT = "api-platform.liruohrh";

    String OSS_LOCAL_URL_PATH_PREFIX = "/oss/static/";
    int ROOT_COMMENT_ROOT_ID = 0;

    String COOKIE_LOGIN_NAME = "api-token";
    String PATTERN_USERNAME = "\\w+?";
    String PATTERN_EMAIL = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
    int CAPTCHA_SIZE = 6;
    int APP_KEY_RANDOM_LEN = 6;
    int APP_SECRET_RANDOM_LEN = 8;

    int MAX_CALL_CAPTCHA = 5;
    int MAX_CALL_VERIFY_CAPTCHA = 5;

    String SESSION_LOGIN_USER = "LOGIN_USER";


    //为了测试改小点
    int PAGE_MAX_SIZE_USER_API_USAGE = 5; //10
    int PAGE_MAX_SIZE_APPLICATION = 5; //20
    int PAGE_MAX_SIZE_USER = 5; //10
    int PAGE_MAX_SIZE_API = 5; //15
    int PAGE_MAX_SIZE_API_SEARCH = 6; //12
    int PAGE_MAX_SIZE_ORDER = 10; //15

    int PAGE_MAX_SIZE_COMMENT = 5; //20
    int PAGE_MAX_SIZE_ORDER_STATUS = 30; //20
}
