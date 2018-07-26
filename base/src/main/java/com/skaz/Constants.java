package com.skaz;

import com.skaz.utils.Charsets;
import com.skaz.utils.Cryptos;
import com.skaz.utils.Envs;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

/**
 * @author jungle
 */
public class Constants {

    public static final String PROFILE_TEST = "test";
    public static final String PROFILE_DEV = "dev";




    public static final Integer ERROR_STATUS_401 = HttpServletResponse.SC_UNAUTHORIZED;
    public static final Integer ERROR_STATUS_403 = HttpServletResponse.SC_FORBIDDEN;
    public static final Integer ERROR_STATUS_500 = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    public static final String APP_SECRET = Cryptos.encryptMd5(Envs.getString("app.secret", "skaz"));


    public static final String SECURITY_SECRET = Envs.getString("app.security.secret", APP_SECRET);

    public static final String ERROR_STATUS_401_MSG = "对不起,请先进行登录或授权!";
    public static final String ERROR_STATUS_401_EXPIRED_MSG = "对不起,您的账号因在其他地方登录或长时间无操作而失效,请重新登录!";
    public static final String SESSION_NAME = Envs.getString("app.security.sessionKty", "sword.session");
    public static final int SECURITY_SESSION_EXPIRE_BROWSER = Envs.getInteger("app.security.session.expire.browser", 60 * 60 * 6);
    public static final String ERROR_STATUS_403_MSG = "对不起,您没有权限访问该资源!";
    public static final String ERROR_STATUS_500_MSG = "对不起,服务发生错误,请稍后再试!";
    public static final boolean APP_DEBUG = Envs.isDebug();
    public static final int PAGE_SIZE = 20;
    /**
     * 编码
     */
    public static final Charset CHARSET = Charsets.UTF_8;
    public static final String CACHE_STORE = Envs.getString("app.name", "sword");

    private static final String LOG_ERROR_FORMAT_CLASS = "错误类名:%s";
    private static final String LOG_ERROR_FORMAT_METHOD = "错误方法:%s";
    private static final String LOG_ERROR_FORMAT_EXCEPTION = "异常信息:%s";


    private static final String LOG_ERROR_FORMAT_URL = "请求地址:%s";
    private static final String LOG_ERROR_FORMAT_PARAM = "请求参数:%s";
    public static final String LOG_ERROR_FORMAT_URL_PARAM = LOG_ERROR_FORMAT_URL + "\n" + LOG_ERROR_FORMAT_PARAM;
    public static final String LOG_ERROR_FORMAT_URL_PARAM_EXCEPTION = LOG_ERROR_FORMAT_URL + "\n" + LOG_ERROR_FORMAT_PARAM + "\n" + LOG_ERROR_FORMAT_EXCEPTION;
    public static final String LOG_ERROR_FORMAT_CLASS_METHOD_EXCEPTION = LOG_ERROR_FORMAT_CLASS + "\n" + LOG_ERROR_FORMAT_METHOD + "\n" + LOG_ERROR_FORMAT_EXCEPTION;

}
