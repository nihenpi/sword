package com.skaz.utils;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类.
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author calvin
 */
public abstract class Reflections {

    public enum Accessible {
        yes, no
    }

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static Logger logger = LoggerFactory.getLogger(Reflections.class);

    public static String getParams(Method method, Object[] args) throws RuntimeException {
        Map<String, Object> params = Maps.newHashMap();
        String[] fieldNames = Reflections.getMethodParamNames(method);
        int i = 0;
        for (String fieldName : fieldNames) {
            params.put(fieldName, Objects.toString(args[i]));
            i++;
        }
        return params.toString();
    }

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
        }
        return object;
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[]{value});
            }
        }
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes, final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {// NOSONAR
                // Field不在当前类定义,继续向上转型
                continue;// new add
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName, final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
                continue;// new add
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     * eg.
     * public UserDao extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    public static Class getClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    public static Class<?> getUserClass(Object instance) {
        Asserts.isNotNull(instance, "Instance must not be null");
        Class clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;

    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    // @auther me
    // ----------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 循环向上转型查找该名称等于fieldName的Field对象
     */
    public static Field getField(final Class<?> clazz, String fieldName, Accessible accessible) {
        Validate.notNull(clazz, "class can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        // Class<?> clazz;
        // if (obj instanceof Class) {
        // clazz = (Class<?>) obj;
        // } else {
        // clazz = obj.getClass();
        // }
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (accessible.equals(Accessible.yes)) {
                makeAccessible(field);
            }
            return field;
        } catch (NoSuchFieldException e) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != Object.class) {
                return getField(superClazz, fieldName);
            } else {
                throw Exceptions.unchecked(e);
            }
        }
    }

    /**
     * 循环向上转型查找该名称等于fieldName的Field对象
     */
    public static Field getField(final Object obj, String fieldName, Accessible accessible) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        return getField(obj.getClass(), fieldName, accessible);
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getField(final Object obj, String fieldName) {
        return getField(obj.getClass(), fieldName, Accessible.yes);
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getField(final Class<?> clazz, String fieldName) {
        return getField(clazz, fieldName, Accessible.yes);
    }

    /**
     * 循环向上转型获取Field对象
     */
    public static List<Field> getFields(final Class<?> clazz) {
        Validate.notNull(clazz, "class can't be null");
        List<Field> fields = Lists.newArrayList();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != Object.class) {
            fields.addAll(getFields(superClazz));
        }
        return fields;
    }

    public static List<Field> getFields(final Object obj) {
        Validate.notNull(obj, "object can't be null");
        return getFields(obj.getClass());
    }

    public static Method getCallingMethod() {
        return getCallingMethod(3);
    }

    public static Class<?> getCallingClass(int n) {
        return getCallingMethod(n).getDeclaringClass();
    }


    public static Method getCallingMethod(int i) {
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

    public static Method getAccessibleMethods(final Object obj, final String methodName, final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");
        Class<?> searchType;
        if (obj instanceof Class) {
            searchType = (Class<?>) obj;
        } else {
            searchType = obj.getClass();
        }
        for (; searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static Class<?> getClass(Object instance) {
        Asserts.isNotNull(instance, "Instance must not be null");
        Class clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }

    public static Class<?> getClass(Method method) {
        Asserts.isNotNull(method, "method must not be null");
        return method.getDeclaringClass();
    }

    public static String reflectionToString(Object obj) {
        return ToStringBuilder.reflectionToString(obj.getClass(), ToStringStyle.MULTI_LINE_STYLE);
    }

    public static String[] getMethodParamNames(Class<?> clazz, String methodName) throws Exception {
        Class<?>[] paramTypes = null;
        return getMethodParamNames(clazz, methodName, paramTypes);
    }

    public static String[] getMethodParamNames(Class<?> clazz, String methodName, Class<?>... paramTypes) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(clazz);
        classPool.insertClassPath(classPath);
        CtClass ctClass = classPool.get(clazz.getName());
        CtMethod ctMethod = null;
        if (paramTypes == null) {
            ctMethod = ctClass.getDeclaredMethod(methodName);
        } else {
            ctMethod = ctClass.getDeclaredMethod(methodName, classPool.get(getParamTypesNames(paramTypes)));
        }
        return getMethodParamNames(ctMethod);
    }

    public static String[] getMethodParamNames(Method method) throws RuntimeException {
        ClassPool classPool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(method.getDeclaringClass());
        classPool.insertClassPath(classPath);
        try {
            CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), classPool.get(getParamTypesNames(method.getParameterTypes())));
            return getMethodParamNames(ctMethod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] getMethodParamNames(CtMethod ctMethod) throws RuntimeException {
        CtClass ctClass = ctMethod.getDeclaringClass();
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            throw new RuntimeException(ctClass.getName());
        }
        try {
            String[] paramNames = new String[ctMethod.getParameterTypes().length];
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = attr.variableName(i + pos);
            }
            return paramNames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getParamTypesNames(Class<?>... paramTypes) {
        String[] paramTypeNames = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypeNames[i] = paramTypes[i].getName();
        }
        return paramTypeNames;
    }

    /**
     * http://tutorials.jenkov.com/java-reflection/generics.html
     *
     * @param method
     * @return
     */
    public static Class<?> getGenericMethodReturnTypes(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                Class<?> typeArgClass = (Class<?>) typeArgument;
                return typeArgClass;
            }
        }
        return null;
    }

    public static Class<?> getGenericMethodParameterTypes(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (Type genericParameterType : genericParameterTypes) {
            if (genericParameterType instanceof ParameterizedType) {
                ParameterizedType aType = (ParameterizedType) genericParameterType;
                Type[] parameterArgTypes = aType.getActualTypeArguments();
                for (Type parameterArgType : parameterArgTypes) {
                    Class<?> parameterArgClass = (Class<?>) parameterArgType;
                    return parameterArgClass;
                }
            }
        }
        return null;
    }

    public static Class<?> getGenericFieldTypes(Field field) {
        Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                Class<?> fieldArgClass = (Class<?>) fieldArgType;
                return fieldArgClass;
            }
        }
        return null;
    }

    /**
     * 判断字段是否为static
     */
    public static boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

}
