package com.skaz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jungle
 */
public abstract class Logs {

    public static final Logger logger = Logs.getLogger(Logs.class);

    private Logs() {
        // EMPTY
    }

    //
    // trace
    // ---------------------------------------------------------------------------------------------------
    public static void trace(final String msg) {
        Logs.trace(getLoggerClass(), msg, new Object[]{});
    }

    public static void trace(final String msg, final Throwable t) {
        Logs.trace(getLoggerClass(), msg, t);
    }

    public static void trace(final Throwable t) {
        Logs.trace(getLoggerClass(), "", t);
    }

    public static void trace(final Object obj, final String msg) {
        Logs.trace(obj, msg, new Object[]{});
    }

    public static void trace(final Object obj, final Throwable t) {
        Logs.trace(obj, "", t);
    }

    public static void trace(final Object obj, final String msg, final Object... args) {
        if (Logs.isTraceEnabled(obj)) {
            Logs.logger(obj).trace(msg, args);
        }
    }

    //
    // debug
    // ---------------------------------------------------------------------------------------------------
    public static void debug(final String msg) {
        Logs.debug(getLoggerClass(), msg, new Object[]{});
    }

    public static void debug(final String msg, final Throwable t) {
        Logs.debug(getLoggerClass(), msg, t);
    }

    public static void debug(final Throwable t) {
        Logs.debug(getLoggerClass(), "", t);
    }

    public static void debug(final Object obj, final String msg) {
        Logs.debug(obj, msg, new Object[]{});
    }

    public static void debug(final Object obj, final Throwable t) {
        Logs.debug(obj, "", t);
    }

    public static void debug(final Object obj, final String msg, final Throwable t) {
        Logs.logger(obj).debug(msg, t);
    }

    public static void debug(final Object obj, final String msg, final Object... args) {
        if (Logs.isDebugEnabled(obj)) {
            Logs.logger(obj).debug(msg, args);
        }
    }

    //
    // info
    // ---------------------------------------------------------------------------------------------------
    public static void info(final String msg) {
        Logs.info(getLoggerClass(), msg, new Object[]{});
    }

    public static void info(final String msg, final Throwable t) {
        Logs.info(getLoggerClass(), msg, t);
    }

    public static void info(final Throwable t) {
        Logs.info(getLoggerClass(), "", t);
    }

    public static void info(final Object obj, final String msg) {
        Logs.info(obj, msg, new Object[]{});
    }

    public static void info(final Object obj, final Throwable t) {
        Logs.info(obj, "", t);
    }

    public static void info(final Object obj, final String msg, final Throwable t) {
        if (Logs.isInfoEnabled(obj)) {
            Logs.logger(obj).info(msg, t);
        }
    }

    public static void info(final Object obj, final String msg, final Object... args) {
        if (Logs.isInfoEnabled(obj)) {
            Logs.logger(obj).info(msg, args);
        }
    }

    //
    // warn
    // ---------------------------------------------------------------------------------------------------
    public static void warn(final String msg) {
        Logs.warn(getLoggerClass(), msg, new Object[]{});
    }

    public static void warn(final String msg, final Throwable t) {
        Logs.warn(getLoggerClass(), msg, t);
    }

    public static void warn(final Throwable t) {
        Logs.warn(getLoggerClass(), "", t);
    }

    public static void warn(final Object obj, final String msg) {
        Logs.warn(obj, msg, new Object[]{});
    }

    public static void warn(final Object obj, final Throwable t) {
        Logs.warn(obj, "", t);
    }

    public static void warn(final Object obj, final String msg, final Throwable t) {
        if (Logs.isWarnEnabled(obj)) {
            Logs.logger(obj).warn(msg, t);
        }
    }

    public static void warn(final Object obj, final String msg, final Object... args) {
        if (Logs.isWarnEnabled(obj)) {
            Logs.logger(obj).warn(msg, args);
        }
    }

    //
    // error
    // ---------------------------------------------------------------------------------------------------
    public static void error(final String msg) {
        Logs.error(getLoggerClass(), msg, new Object[]{});
    }

    public static void error(final String msg, final Throwable t) {
        Logs.error(getLoggerClass(), msg, t);
    }

    public static void error(final Throwable t) {
        Logs.error(getLoggerClass(), "", t);
    }

    public static void error(final Object obj, final String msg) {
        Logs.error(obj, msg, new Object[]{});
    }

    public static void error(final Object obj, final Throwable t) {
        Logs.error(obj, "", t);
    }

    public static void error(final Object obj, final String msg, Throwable t) {
        if (Logs.isErrorEnabled(obj)) {
            Logs.logger(obj).error(msg, t);
        }
    }

    public static void error(final Object obj, final String msg, final Object... args) {
        if (Logs.isErrorEnabled(obj)) {
            Logs.logger(obj).error(msg, args);
        }
    }

    //
    // other
    // ---------------------------------------------------------------------------------------------------

    public static boolean isTraceEnabled(final Object obj) {
        return Logs.logger(obj).isTraceEnabled();
    }

    public static boolean isDebugEnabled(final Object obj) {
        return Logs.logger(obj).isDebugEnabled();
    }

    public static boolean isInfoEnabled(final Object obj) {
        return Logs.logger(obj).isInfoEnabled();
    }

    public static boolean isWarnEnabled(final Object obj) {
        return Logs.logger(obj).isWarnEnabled();
    }

    public static boolean isErrorEnabled(final Object obj) {
        return Logs.logger(obj).isErrorEnabled();
    }

    public static boolean isDebug() {
        //        return (logger.isInfoEnabled() && (logger.isWarnEnabled() || logger.isErrorEnabled()));
        return logger.isDebugEnabled();
    }

    public static void method() {
        Method m = getCallingMethodOfLogs();
        Class<?> c = m.getDeclaringClass();
        StringBuilder sb = new StringBuilder();
        sb.append(c.getSimpleName());
        sb.append(".");
        sb.append(m.getName());
        if (isDebug()) {
            System.out.println(sb.toString());
        } else {
            Logs.info(Logs.class, sb.toString());
        }
    }

    public static void msg(Object obj) {
        if (null == obj) {
            System.out.println("null");
        } else if (obj instanceof Map) {
            System.out.println(FastJsons.toJSONString(obj, false));
        } else if (obj instanceof List) {
            List<?> objs;
            if (obj instanceof LinkedList<?>) {
                objs = (LinkedList<?>) obj;
            } else {
                objs = (List<?>) obj;
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < objs.size(); i++) {
                Object o = objs.get(i);
                //                sb.append(Objects.toStringWithToStringBuilder(o, ToStringStyle.MULTI_LINE_STYLE));
                sb.append(FastJsons.toJSONString(obj, false));
                if (i < objs.size() - 1) {
                    sb.append("\n");
                }
            }
            System.out.println(sb);
        } else if (obj instanceof String) {
            String msg = (String) obj;
            if (isDebug()) {
                System.out.println(msg);
            } else {
                Logs.debug(Logs.class, msg);
            }
        } else {
            //            String msg = Objects.toStringWithToStringBuilder(obj, ToStringStyle.MULTI_LINE_STYLE);
            String msg = FastJsons.toJSONString(obj, false);
            msg(msg);
        }
    }

    public static void msgs(Object... obj) {
        //        msg(Objects.toStringWithToStringBuilder(obj, ToStringStyle.MULTI_LINE_STYLE));
        msg(FastJsons.toJSONString(obj, false));

    }

    public static void msgr(Object obj) {
        msg(obj);
        printHr();
    }

    public static void printHr(String name) {
        if (isDebug()) {
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            if (name != null) {
                System.out.println(name);
            }
        }
    }

    public static void printHr() {
        printHr(null);
    }

    // ---------------------------------------------------------------------------------------------------
    private static StackTraceElement getCallInfo() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement callInfo = stackTraceElements[3];
        return callInfo;
    }

    private static Object getLoggerClass() {
        StackTraceElement callInfo = getCallInfo();
        try {
            return Class.forName(callInfo.getClassName());
        } catch (Exception ignored) {
        }
        return Logs.class;
    }

    public static String getLoggerMethod() {
        return getCallInfo().getMethodName();
    }

    public static void printLoggerClassInfo() {
        // "yyyy-MM-dd HH:mm:ss [ClassName]-[MethodName]-[LineNumber]-[ThreadName] logback";
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement callInfo = stackTraceElements[1];

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[" + callInfo.getClassName() + "]") //
                .append("-") //
                .append("[" + callInfo.getMethodName() + "]") //
                .append("-") //
                .append("[" + callInfo.getLineNumber() + "]") //
                .append("-") //
                .append("[" + Thread.currentThread().getName() + "]").append(" ");
        System.out.println(stringBuffer.toString());
    }

    public static Class<?> getCallingClass() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logs.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (callerClassName == null) {
                    callerClassName = ste.getClassName();
                } else if (!callerClassName.equals(ste.getClassName())) {
                    return ste.getClass();
                }
            }
        }
        return null;
    }

    public static Method getCallingMethodOfLogs() {
        return getCallingMethodWithLevel(4);
    }

    public static Method getCallingMethodWithLevel(int i) {
        final Thread t = Thread.currentThread();
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final StackTraceElement ste = stackTrace[i];
        final String methodName = ste.getMethodName();
        final String className = ste.getClassName();
        try {
            Class<?> kls = Class.forName(className);
            do {
                for (final Method candidate : kls.getDeclaredMethods()) {
                    if (candidate.getName().equals(methodName)) {
                        return candidate;
                    }
                }
                kls = kls.getSuperclass();
            } while (kls != null);
        } catch (Exception e) {
            Exceptions.unchecked(e);
        }
        return null;
    }

    private static Logger logger(final Object obj) {
        Logger logger;
        if (obj instanceof Class) {
            logger = LoggerFactory.getLogger((Class<?>) obj);
        } else {
            logger = LoggerFactory.getLogger(obj.getClass());
        }
        return logger;
    }

    public static Logger getLogger(final Object obj) {
        return Logs.logger(obj);
    }

}
