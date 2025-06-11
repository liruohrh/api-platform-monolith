declare namespace API {
  type AdminApiSearchReq = {
    current?: number;
    score?: boolean;
    orderVolume?: boolean;
    ctimeS?: boolean;
    utimeS?: boolean;
    id?: number;
    name?: string;
    description?: string;
    method?: string;
    protocol?: string;
    domain?: string;
    path?: string;
    status?: number;
    ctime?: number[];
    utime?: number[];
  };

  type ApiAndUsageVo = {
    id?: number;
    logoUrl?: string;
    name?: string;
    description?: string;
    method?: string;
    protocol?: string;
    domain?: string;
    path?: string;
    price?: number;
    freeTimes?: number;
    orderVolume?: number;
    score?: number;
    status?: number;
    ctime?: number;
    utime?: number;
    leftTimes?: number;
  };

  type ApiCallResp = {
    status?: number;
    body?: string;
    headers?: { all?: Record<string, any>; empty?: boolean };
  };

  type apiOrderParams = {
    req: ApiOrderStatisticsReq;
  };

  type ApiOrderStatisticsReq = {
    year?: number;
    month?: number;
    apiId?: number;
    userId?: number;
  };

  type ApiOrderStatisticsResp = {
    date?: string;
    amount?: number;
    total?: number;
  };

  type ApiOrderStatisticsVo = {
    status?: ApiOrderStatisticsResp[];
    amount?: number;
    total?: number;
  };

  type ApiSearchReq = {
    key?: string;
    current?: number;
    isFree?: boolean;
    price?: boolean;
    orderVolume?: boolean;
    score?: boolean;
    ctime?: boolean;
  };

  type ApiSearchVo = {
    id?: number;
    logoUrl?: string;
    name?: string;
    description?: string;
    price?: number;
    orderVolume?: number;
    score?: number;
    ctime?: number;
  };

  type ApiStatus = {
    id?: number;
    createDate?: string;
    userId?: number;
    apiId?: number;
    callTimes?: number;
    successTimes?: number;
    totalDuration?: number;
  };

  type ApiStatusVo = {
    status?: ApiStatus[];
    callTimes?: number;
    successTimes?: number;
    totalDuration?: number;
  };

  type apiUsageParams = {
    req: ApiUsageReq;
  };

  type ApiUsageReq = {
    year?: number;
    month?: number;
    username?: string;
    apiName?: string;
  };

  type Application = {
    id?: number;
    reporterId?: number;
    applicationType?: number;
    reason?: string;
    description?: string;
    replyContent?: string;
    auditStatus?: number;
    extraData?: string;
    ctime?: number;
    utime?: number;
  };

  type ApplicationAddReq = {
    reason: string;
    description: string;
  };

  type ApplicationAuditReq = {
    replyContent?: string;
    auditStatus: 'PENDING_AUDIT' | 'APPROVED' | 'NOT_PASS' | 'CANCEL';
  };

  type auditApplicationParams = {
    id: number;
  };

  type callAPIParams = {
    apiId: number;
  };

  type cancelRefundParams = {
    orderUid: string;
  };

  type coldJokeParams = {
    description?: string;
  };

  type Comment = {
    id?: number;
    apiId?: number;
    userId?: number;
    score?: number;
    content?: string;
    replyToCommentId?: number;
    replyToUserId?: number;
    rootCommentId?: number;
    favorCount?: number;
    replyCount?: number;
    ctime?: number;
    deleted?: number;
  };

  type CommentAddReq = {
    apiId: number;
    orderId: number;
    score: number;
    content: string;
  };

  type CommentPageVo = {
    score?: number;
    apiName?: string;
    username?: string;
    userNickname?: string;
    content?: string;
    ctime?: number;
    favorCount?: number;
    replyCount?: number;
    id?: number;
    apiId?: number;
    userId?: number;
    isRoot?: boolean;
    adminReplies?: CommentVo[];
  };

  type CommentReplyReq = {
    apiId?: number;
    replyToCommentId?: number;
    content?: string;
  };

  type CommentReplyVo = {
    id?: number;
    apiId?: number;
    userId?: number;
    userNickname?: string;
    apiName?: string;
    content?: string;
    ctime?: number;
    favorCount?: number;
    replyCount?: number;
    userComment?: CommentVo;
    isRead?: boolean;
    isFavor?: boolean;
  };

  type CommentReportApplicationVo = {
    id?: number;
    applicationType?: number;
    reason?: string;
    description?: string;
    replyContent?: string;
    apiId?: number;
    apiName?: string;
    auditStatus?: number;
    reportedComment?: CommentVo;
    reporterId?: number;
    reporterNickname?: string;
    reporterUsername?: string;
    ctime?: number;
    utime?: number;
  };

  type CommentVo = {
    id?: number;
    apiId?: number;
    userId?: number;
    userNickName?: string;
    username?: string;
    score?: number;
    content?: string;
    ctime?: number;
    replyToCommentId?: number;
    replyToUserId?: number;
    replyToUserNickname?: string;
    replyToUsername?: string;
    rootCommentId?: number;
    favorCount?: number;
    replyCount?: number;
    isFavor?: boolean;
    subCommentList?: CommentVo[];
  };

  type CommonPageReq = {
    current: number;
    size?: number;
  };

  type currentWeekWeatherParams = {
    req: WeatherReq;
  };

  type deleteAPIParams = {
    apiId: number;
  };

  type deleteCommentParams = {
    id: number;
  };

  type deleteUserApiUsageParams = {
    id: number;
  };

  type favorCommentParams = {
    id: number;
  };

  type getAPIByIdParams = {
    apiId: number;
  };

  type getCaptchaParams = {
    email: string;
  };

  type getCommentParams = {
    id: number;
  };

  type getOrderByIdParams = {
    orderId: string;
  };

  type getUserByIdParams = {
    userId: number;
  };

  type HttpApiAddReq = {
    description: string;
    logoUrl?: string;
    name: string;
    domain?: string;
    protocol: string;
    path: string;
    method: 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
    params?: string;
    reqHeaders?: string;
    reqBody?: string;
    respHeaders?: string;
    respBody?: string;
    respSuccess?: string;
    respFail?: string;
    errorCodes?: string;
    price: number;
    freeTimes?: number;
  };

  type HttpApiResp = {
    id?: number;
    ownerId?: number;
    description?: string;
    name?: string;
    logoUrl?: string;
    method?: string;
    protocol?: string;
    domain?: string;
    path?: string;
    params?: string;
    reqHeaders?: string;
    reqBody?: string;
    respHeaders?: string;
    respBody?: string;
    respSuccess?: string;
    respFail?: string;
    errorCodes?: string;
    price?: number;
    freeTimes?: number;
    orderVolume?: number;
    score?: number;
    status?: number;
    ctime?: number;
    utime?: number;
  };

  type HttpApiUpdateReq = {
    id: number;
    description?: string;
    logoUrl?: string;
    name?: string;
    status?: number;
    domain?: string;
    protocol: string;
    path: string;
    method?: 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
    params?: string;
    reqHeaders?: string;
    reqBody?: string;
    respHeaders?: string;
    respBody?: string;
    respSuccess?: string;
    respFail?: string;
    errorCodes?: string;
    price?: number;
    freeTimes?: number;
  };

  type isFreeUsedParams = {
    apiId: number;
  };

  type launchAPIParams = {
    apiId: number;
  };

  type listAllCommentParams = {
    current: number;
    ctime?: boolean;
    favorCount?: boolean;
    replyCount?: boolean;
    rootCommentId?: number;
    isUserReply?: boolean;
    username?: string;
    apiId?: number;
    replyToCommentId?: number;
    replyToMe?: boolean;
    isRoot?: boolean;
    excludeMe?: boolean;
  };

  type listAPIParams = {
    req: AdminApiSearchReq;
  };

  type ListApplicationReq = {
    current?: number;
    reporterName?: string;
    reason?: string;
    auditStatus?: number;
    ctimeRange?: number[];
    ctimeS?: boolean;
  };

  type listCommentParams = {
    apiId?: number;
    rootId?: number;
    commonPageReq: CommonPageReq;
  };

  type listCommentReportApplicationParams = {
    req: ListApplicationReq;
  };

  type listOrderStatusParams = {
    req: ListOrderStatusReq;
  };

  type ListOrderStatusReq = {
    current?: number;
    isMonth?: boolean;
    dateRange?: number[];
    amountS?: boolean;
    totalS?: boolean;
  };

  type listReplyToUserCommentParams = {
    commonPageReq: CommonPageReq;
  };

  type listUserApiAndUsageParams = {
    req: UserApiSearchReq;
  };

  type listUserApiUsageParams = {
    req: UserApiUsageReq;
  };

  type listUserCommentParams = {
    commonPageReq: CommonPageReq;
  };

  type listUserParams = {
    req: UserSearchReq;
  };

  type optForOrderParams = {
    orderId: string;
    isPay?: boolean;
    isCancel?: boolean;
  };

  type OrderCreateReq = {
    apiId: number;
    amount?: number;
    free?: boolean;
  };

  type OrderNumber = {
    value?: number;
    min?: number;
    minEq?: boolean;
    max?: number;
    maxEq?: boolean;
  };

  type OrderRefundReq = {
    orderId: string;
    reason: string;
    description?: string;
  };

  type OrderSearchReq = {
    orderId?: string;
    apiId?: number;
    userId?: number;
    actualPayment?: OrderNumber;
    apiName?: string;
    status?: number;
    price?: number;
    ctime?: OrderNumber;
    utime?: OrderNumber;
  };

  type OrderSortReq = {
    actualPayment?: boolean;
    amount?: boolean;
    ctimeS?: boolean;
    utimeS?: boolean;
  };

  type OrderStatusVo = {
    date?: string;
    apiId?: number;
    apiName?: string;
    amount?: number;
    total?: number;
  };

  type OrderVo = {
    id?: number;
    orderId?: string;
    apiId?: number;
    api?: HttpApiResp;
    apiName?: string;
    userId?: number;
    username?: string;
    userNickname?: string;
    actualPayment?: number;
    amount?: number;
    isUsed?: boolean;
    status?: number;
    application?: Application;
    price?: number;
    isComment?: boolean;
    ctime?: number;
    utime?: number;
  };

  type PageReqOrderSearchReqOrderSortReq = {
    search?: OrderSearchReq;
    current?: number;
    size?: number;
    sort?: OrderSortReq;
    isFree?: boolean;
  };

  type PageRespApiAndUsageVo = {
    data?: ApiAndUsageVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespApiSearchVo = {
    data?: ApiSearchVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespCommentPageVo = {
    data?: CommentPageVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespCommentReplyVo = {
    data?: CommentReplyVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespCommentReportApplicationVo = {
    data?: CommentReportApplicationVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespCommentVo = {
    data?: CommentVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespHttpApiResp = {
    data?: HttpApiResp[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespOrderStatusVo = {
    data?: OrderStatusVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespOrderVo = {
    data?: OrderVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespUserApiUsageVo = {
    data?: UserApiUsageVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespUserCommentVo = {
    data?: UserCommentVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespUserVo = {
    data?: UserVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type readReplyParams = {
    commentId?: number;
  };

  type reportCommentParams = {
    commentId: number;
  };

  type RespApiCallResp = {
    code?: number;
    msg?: string;
    data?: ApiCallResp;
  };

  type RespApiOrderStatisticsVo = {
    code?: number;
    msg?: string;
    data?: ApiOrderStatisticsVo;
  };

  type RespApiStatusVo = {
    code?: number;
    msg?: string;
    data?: ApiStatusVo;
  };

  type RespBoolean = {
    code?: number;
    msg?: string;
    data?: boolean;
  };

  type RespComment = {
    code?: number;
    msg?: string;
    data?: Comment;
  };

  type RespCommentVo = {
    code?: number;
    msg?: string;
    data?: CommentVo;
  };

  type RespHttpApiResp = {
    code?: number;
    msg?: string;
    data?: HttpApiResp;
  };

  type RespListHttpApiResp = {
    code?: number;
    msg?: string;
    data?: HttpApiResp[];
  };

  type RespListLong = {
    code?: number;
    msg?: string;
    data?: number[];
  };

  type RespLong = {
    code?: number;
    msg?: string;
    data?: number;
  };

  type RespOrderVo = {
    code?: number;
    msg?: string;
    data?: OrderVo;
  };

  type RespPageRespApiAndUsageVo = {
    code?: number;
    msg?: string;
    data?: PageRespApiAndUsageVo;
  };

  type RespPageRespApiSearchVo = {
    code?: number;
    msg?: string;
    data?: PageRespApiSearchVo;
  };

  type RespPageRespCommentPageVo = {
    code?: number;
    msg?: string;
    data?: PageRespCommentPageVo;
  };

  type RespPageRespCommentReplyVo = {
    code?: number;
    msg?: string;
    data?: PageRespCommentReplyVo;
  };

  type RespPageRespCommentReportApplicationVo = {
    code?: number;
    msg?: string;
    data?: PageRespCommentReportApplicationVo;
  };

  type RespPageRespCommentVo = {
    code?: number;
    msg?: string;
    data?: PageRespCommentVo;
  };

  type RespPageRespHttpApiResp = {
    code?: number;
    msg?: string;
    data?: PageRespHttpApiResp;
  };

  type RespPageRespOrderStatusVo = {
    code?: number;
    msg?: string;
    data?: PageRespOrderStatusVo;
  };

  type RespPageRespOrderVo = {
    code?: number;
    msg?: string;
    data?: PageRespOrderVo;
  };

  type RespPageRespUserApiUsageVo = {
    code?: number;
    msg?: string;
    data?: PageRespUserApiUsageVo;
  };

  type RespPageRespUserCommentVo = {
    code?: number;
    msg?: string;
    data?: PageRespUserCommentVo;
  };

  type RespPageRespUserVo = {
    code?: number;
    msg?: string;
    data?: PageRespUserVo;
  };

  type RespString = {
    code?: number;
    msg?: string;
    data?: string;
  };

  type RespUser = {
    code?: number;
    msg?: string;
    data?: User;
  };

  type RespUserVo = {
    code?: number;
    msg?: string;
    data?: UserVo;
  };

  type RespVoid = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type RespWeatherResp = {
    code?: number;
    msg?: string;
    data?: WeatherResp;
  };

  type rollOffAPIParams = {
    apiId: number;
  };

  type searchAPIParams = {
    req: ApiSearchReq;
  };

  type updateUserApiUsageParams = {
    id: number;
  };

  type User = {
    id?: number;
    nickname?: string;
    username?: string;
    passwd?: string;
    email?: string;
    personalDescription?: string;
    avatarUrl?: string;
    appKey?: string;
    appSecret?: string;
    apiPrefix?: string;
    role?: string;
    status?: number;
    ctime?: number;
    deleted?: number;
  };

  type UserAddReq = {
    nickname: string;
    username?: string;
    passwd?: string;
    email?: string;
    role: string;
  };

  type UserApiSearchReq = {
    key?: string;
    current?: number;
    isFree?: boolean;
    price?: boolean;
    leftTimes?: boolean;
  };

  type UserApiUsageReq = {
    username?: string;
    apiName?: string;
    excludeFreeApi?: boolean;
    current?: number;
  };

  type UserApiUsageUpdateReq = {
    leftTimes?: number;
    freeUsed?: boolean;
  };

  type UserApiUsageVo = {
    id?: number;
    isFreeApi?: boolean;
    apiId?: number;
    apiName?: string;
    userId?: number;
    username?: string;
    userNickname?: string;
    leftTimes?: number;
    freeUsed?: boolean;
  };

  type UserCommentVo = {
    id?: number;
    apiId?: number;
    apiName?: string;
    score?: number;
    content?: string;
    ctime?: number;
    rootCommentId?: number;
    favorCount?: number;
    replyCount?: number;
    isReply?: boolean;
    addresseeNickname?: string;
    addresseeComment?: CommentVo;
  };

  type UserLoginReq = {
    loginType: 'CODE' | 'PASSWD';
    username?: string;
    passwd?: string;
    email?: string;
    captcha?: string;
  };

  type UserNewPasswdReq = {
    newPasswd?: string;
    email?: string;
    captcha?: string;
  };

  type UserRegisterReq = {
    nickname: string;
    username?: string;
    passwd?: string;
    email?: string;
    captcha?: string;
  };

  type UserSearchReq = {
    current?: number;
    id?: number;
    nickname?: string;
    username?: string;
    email?: string;
    personalDescription?: string;
    appKey?: string;
    role?: string;
    status?: number;
    ctime?: number[];
    ctimeS?: boolean;
  };

  type UserUpdateReq = {
    id: number;
    avatarUrl?: string;
    nickname?: string;
    username?: string;
    personalDescription?: string;
    email?: string;
    captcha?: string;
    role?: string;
    status?: number;
  };

  type UserVo = {
    id?: number;
    nickname?: string;
    username?: string;
    email?: string;
    personalDescription?: string;
    avatarUrl?: string;
    appKey?: string;
    role?: string;
    status?: number;
    ctime?: number;
  };

  type WeatherReq = {
    city?: string;
    adcode?: string;
    ip?: string;
  };

  type WeatherResp = {
    location?: string;
    date?: string;
    description?: string;
    lowerT?: string;
    highT?: string;
  };
}
