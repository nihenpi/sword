package com.skaz.web.advice;

import com.skaz.App;
import com.skaz.Constants;
import com.skaz.bean.Result;
import com.skaz.exception.SystemException;
import com.skaz.utils.Exceptions;
import com.skaz.utils.Jsons;
import com.skaz.utils.Logs;
import com.skaz.utils.Results;
import com.skaz.utils.Servlets;
import com.skaz.utils.Strings;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author jungle
 * controller层的全局异常处理
 */
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Result exception(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String url = Servlets.getRequestURL(request);
        String params = Jsons.toJSONString(request.getParameterMap());
        String msgLog = null;

        Throwable cause = Exceptions.getRootCause(ex);
        Integer status = Exceptions.determinResponseStatus(cause);
        String msgOut = Exceptions.determinResponseMessage(cause);
        if (!Exceptions.isSecurityException(cause)) {
            if (cause instanceof Exception) {
                String msgLogUrl = Strings.format(Constants.LOG_ERROR_FORMAT_URL_PARAM, url, params);
                if (ex instanceof SystemException) {
                    msgLog = msgLogUrl + "\n" + ex.getMessage();
                } else {
                    if (null != handler && (handler instanceof HandlerMethod)) {
                        Method handlerMethod = ((HandlerMethod) handler).getMethod();
                        String method = handlerMethod.getName();
                        String clazz = handlerMethod.getDeclaringClass().getSimpleName();
                        msgLog = "\n" + msgLogUrl + "\n" + Strings.format(Constants.LOG_ERROR_FORMAT_CLASS_METHOD_EXCEPTION, clazz, method, ex.getClass().getName() + ":" + ex.getMessage());
                    } else {
                        // 获取不到对应的Handler时候，则只记录URL和PARAM
                        msgLog = Strings.format(Constants.LOG_ERROR_FORMAT_URL_PARAM_EXCEPTION, url, params, ex.getClass().getName() + ":" + ex.getMessage());
                    }
                }
            }
            if (Constants.APP_DEBUG) {
                msgLog += "\n返回状态:" + status + "\n返回消息:" + msgOut;
                msgOut = msgLog;
            }
            Logs.error(App.class, "\n" + msgLog, cause);
        }
        return Results.failure(status, msgOut);
    }


}
