export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册', path: '/user/register', component: './User/Register' },
      { name: '重置密码', path: '/user/passwd', component: './User/Passwd' },
    ],
  },
  { path: '/welcome', icon: 'smile', component: './Welcome' },
  {
    name: '接口广场',
    icon: 'reddit',
    path: '/api/list',
    component: './API/List',
    access: 'isUserMode',
  },
  { path: '/api/info/:id', component: './API/$id' },
  {
    path: '/order/list',
    component: './Order/List',
    access: 'isLoginUser',
  },
  { path: '/order/:id', component: './Order/$id' },
  { path: '/order/pay/:id', component: './Order/Pay/$id' },
  //无name就不显示侧边栏菜单
  { path: '/account/center', component: './Account/Center' },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
  // {
  //   path: '/admin',
  //   access: 'canAdmin',
  //   routes: [
  //     // { path: '/admin', redirect: '/admin/sub-page' },
  //     // { path: '/admin/demo', name: '管理Demo', component: './Admin/Demo' },
  //     // {
  //     //   path: '/admin/dashboard',
  //     //   name: 'Dashboard',
  //     //   routes: [
  //     //     {
  //     //       path: '/admin/dashboard/api-usage',
  //     //       name: 'API',
  //     //       component: './Admin/Dashboard/ApiUsage',
  //     //     },
  //     //     {
  //     //       path: '/admin/dashboard/order',
  //     //       name: '订单',
  //     //       component: './Admin/Dashboard/ApiOrder',
  //     //     },
  //     //   ],
  //     // },
  //   ],
  // },
  {
    access: 'isAdminMode',
    path: '/admin/user',
    name: '用户管理',
    component: './Admin/User',
  },
  {
    access: 'isAdminMode',
    path: '/admin/api',
    name: 'API管理',
    routes: [
      { path: '/admin/api/manage', name: 'API信息管理', component: './Admin/API/Manage' },
      {
        path: '/admin/api/stats',
        name: 'API调用情况统计',
        component: './Admin/API/Stats',
      },
      {
        path: '/admin/api/usage/user',
        name: 'API用户使用情况统计',
        component: './Admin/API/UserUsage',
      },
    ],
  },
  {
    access: 'isAdminMode',
    path: '/admin/order',
    name: '订单管理',
    routes: [
      { path: '/admin/order/manage', name: '订单信息管理', component: './Admin/Order/Manage' },
      {
        path: '/admin/order/transaction',
        name: '销售额统计',
        component: './Admin/Order/Transaction',
      },
    ],
  },
  // { path: '/admin/comment', name: '管理评论', component: './Admin/Comment' },
  // { path: '/admin/comment/report', name: '管理评论举报', component: './Admin/CommentReport' },
  {
    access: 'isAdminMode',
    path: '/admin/comment',
    name: '评论管理',
    routes: [
      { path: '/admin/comment/manage', name: '评论信息管理', component: './Admin/Comment/Manage' },
      {
        path: '/admin/comment/report',
        name: '评论举报管理',
        component: './Admin/Comment/Report',
      },
    ],
  },
];
