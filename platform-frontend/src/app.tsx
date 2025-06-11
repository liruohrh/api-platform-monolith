import { AvatarDropdown, AvatarName, Footer } from '@/components';
import { LinkOutlined } from '@ant-design/icons';
import type { Settings as LayoutSettings } from '@ant-design/pro-components';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { history, Link } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { getLoginUser } from '@/services/api-platform/authController';
import { requestConfig } from '@/requestConfig';
import { loginPath, NeedLogin } from '@/constants';
const isDev = process.env.NODE_ENV === 'development';


/**
 * hook: useModel('@@initialState');
 * @example
 *  const initState = {
 *   initialState: undefined as InitialStateType,
 *   loading: true,
 *   error: undefined,
 *   //将重新调用getInitialState，也会重新执行layout，凡是用到useModel('@@initialState')的地方都会重新渲染
 *   //  如果大部分是用户信息数据还好，因为本来就是更新用户数据
 *   refresh: ()=>void
 *   //设置initialState
 *   setInitialState: (initialState: InitialStateType | ((initialState: InitialStateType) => InitialStateType)) =>void
 *
 *  };
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.User;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.User | undefined>;
}> {
  const fetchUserInfo = async () => {
    try {
      const data = await getLoginUser({
        skipErrorHandler: true
      });
      return data?.data;
    } catch (error) {
      console.log(error);
      // history.push(loginPath);
    }
    return undefined;
  };
  const currentUser = await fetchUserInfo();
  console.log("getInitialState", defaultSettings);
  const layout = currentUser?.role === "ADMIN" && localStorage.getItem("isAdminMode") === "true" ? "side" : "top";
  return {
    fetchUserInfo,
    currentUser: currentUser,
    settings: {
      ...defaultSettings,
      layout
    } as Partial<LayoutSettings>,
  };
}

/**
 * 点击a、地址栏输入 时并不会重新执行，手动调用history.push才重新执行
 * 什么时候执行：比如
 *    1. 注册页跳转主页： history.push(/)
 *      layout(/), onPageChange(/welcome)
 *    2. 登录后跳转主页：history.push(/)
 *      layout(/), onPageChange(/),
 *      layout(/welcome), onPageChange(/welcome),
 *      layout(/welcome)（明显是多余的，但是不知道怎么改）
 * 不知道为什么:
 *  1.
 *    主页刷新：layout(/welcome),layout(/welcome), onPageChange(/welcome), layout(/welcome)
 *    登录页刷新却：啥也没有
 *
 * bug:
 *   注册页跳转主页：layout(/),onPageChange(/)
 *     由于onPageChange使用的是layout传递的值，
 *     而layout使用的是旧layout的值（可能是因为页面没有刷新，状态不变，就没有重新执行getInitialState），
 *     因此被onPageChange的逻辑（未登录跳转登录页）就跳转到登录页去了
 *     解决非法：在注册页调用 setInitialState
 *
 * @param initialState 当前页面的initialState
 *  问题
 *      1. 登录页设置initialState后，跳转页面不会重新执行 getInitialState
 *        可能是因为本来就是一个页面，没有刷新，因此状态仍然被保存
 * @param setInitialState
 */
// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, setInitialState }) => {
  return {
    // actionsRender: () => [<Question key="doc" />],
    avatarProps: {
      src: initialState?.currentUser?.avatarUrl,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.nickname,
    },
    footerRender: () => <Footer />,
    /**
     * 虽然可以做登录拦截，但是还是会先展示页面，因此如果需要用户数据的地方可以显示loading，比如<Spin />
     * @example
     * useEffect(() => {
     *     props.onPageChange?.(props.location);
     *     // eslint-disable-next-line react-hooks/exhaustive-deps
     *   }, [location.pathname, location.pathname?.search]);
     * @param location：引起history变化的值，即新history.location
     */
    onPageChange: (location) => {

      if (!initialState?.currentUser
        && history.location.pathname !== loginPath
        && NeedLogin(location?.pathname ?? "")
      ) {

        history.replace(loginPath);
        // history.push(loginPath);
      }
    },
    bgLayoutImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    // links: isDev
    //   ? [
    //     <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
    //       <LinkOutlined />
    //       <span>OpenAPI 文档</span>
    //     </Link>,
    //   ]
    //   : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};



/**
 * 必须在这里导出
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = requestConfig;
