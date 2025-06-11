/**
 * @see https://umijs.org/docs/max/access#access
 * 1. 给路由添加属性access
 * 2. 路由的access属性值就是 access函数返回的对象key
 * */
export default function access(
  initialState: { currentUser?: API.User; settings?: any } | undefined,
) {
  const { currentUser } = initialState ?? {};
  const canAdmin = currentUser?.role === 'ADMIN';
  const isAdminMode =
    currentUser?.role === 'ADMIN' && localStorage.getItem('isAdminMode') === 'true';
  return {
    canAdmin: canAdmin,
    isLoginUser: currentUser != null || currentUser != undefined,
    isUserMode: !isAdminMode,
    isAdminMode: isAdminMode,
  };
}
