package com.skaz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 代码拷贝来自：sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
 * @author jungle
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    /**
     * 实际对象 Map<String,User>里的User.class
     */
    private Type[] actualTypeArguments;

    /**
     * 原始对象 Map<String,User>里的Map.class
     */
    private Class<?> rawType;

    /**
     * 用于这个泛型上中包含了内部类的情况,一般返回null
     */
    private Type ownerType;

    public static ParameterizedTypeImpl make(Class<?> rawClass, Type actualType) {
        return new ParameterizedTypeImpl(rawClass, new Type[]{actualType}, null);
    }

    public static ParameterizedTypeImpl make(Class<?> rawClass, Type actualType, Type ownerType) {
        return new ParameterizedTypeImpl(rawClass, new Type[]{actualType}, ownerType);
    }

    public static ParameterizedTypeImpl make(Class<?> rawClass, Type[] actualTypes, Type ownerType) {
        return new ParameterizedTypeImpl(rawClass, actualTypes, ownerType);
    }

    public static ParameterizedTypeImpl makeIn(Class<?> rawClass, Class<?> actualTypeInClass, Type actualType) {
        Type type = new ParameterizedTypeImpl(actualTypeInClass, new Type[]{actualType}, null);
        return new ParameterizedTypeImpl(rawClass, new Type[]{type}, null);
    }

    public static ParameterizedTypeImpl makeIn(Class<?> rawClass, Class<?> actualTypeInClass, Type keyType, Type valueType) {
        Type type = new ParameterizedTypeImpl(actualTypeInClass, new Type[]{keyType, valueType}, null);
        return new ParameterizedTypeImpl(rawClass, new Type[]{type}, null);
    }

    /**
     * List<User>
     */
    public static ParameterizedTypeImpl makeInList(Type actualType) {
        return make(List.class, actualType);
    }

    /**
     * RawClass<List<actualType>>
     */
    public static ParameterizedTypeImpl makeInList(Class<?> rawClass, Type actualType) {
        return makeIn(rawClass, List.class, actualType);
    }

    public static ParameterizedTypeImpl makeInMap(Class<?> rawClass, Type keyType, Type valueType) {
        return makeIn(rawClass, Map.class, keyType, valueType);
    }

    public static <K, V> Map<K, V> parseToMap(String jsonString, Class<K> keyType, Class<V> valueType) {
        if (null == jsonString || null == keyType || null == valueType) {
            return null;
        }
        return JSON.parseObject(jsonString, new TypeReference<Map<K, V>>(keyType, valueType) {});
    }

    private ParameterizedTypeImpl(Class<?> rawClass, Type[] actualTypes, Type ownerType) {
        this.actualTypeArguments = actualTypes;
        this.rawType = rawClass;
        if (ownerType != null) {
            this.ownerType = ownerType;
        } else {
            this.ownerType = rawClass.getDeclaringClass();
        }
        validateConstructorArguments();
    }

    private void validateConstructorArguments() {
        @SuppressWarnings("rawtypes") TypeVariable[] arrayOfTypeVariable = this.rawType.getTypeParameters();
        if (arrayOfTypeVariable.length != this.actualTypeArguments.length) {
            throw new MalformedParameterizedTypeException();
        }
        for (int i = 0; i < this.actualTypeArguments.length; i++) {
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return (Type[]) this.actualTypeArguments.clone();
    }

    @Override
    public Class<?> getRawType() {
        return this.rawType;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override
    public boolean equals(Object paramObject) {
        if ((paramObject instanceof ParameterizedType)) {
            ParameterizedType localParameterizedType = (ParameterizedType) paramObject;

            if (this == localParameterizedType) {
                return true;
            }
            Type localType1 = localParameterizedType.getOwnerType();
            Type localType2 = localParameterizedType.getRawType();

            return (this.ownerType == null ? localType1 == null : this.ownerType.equals(localType1)) && (this.rawType == null ? localType2 == null : this.rawType.equals(localType2)) && (Arrays.equals(this.actualTypeArguments, localParameterizedType.getActualTypeArguments()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.actualTypeArguments) ^ (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ (this.rawType == null ? 0 : this.rawType.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder localStringBuilder = new StringBuilder();

        if (this.ownerType != null) {
            if ((this.ownerType instanceof Class<?>)) {
                localStringBuilder.append(((Class<?>) this.ownerType).getName());
            } else {
                localStringBuilder.append(this.ownerType.toString());
            }
            localStringBuilder.append(".");

            if ((this.ownerType instanceof ParameterizedTypeImpl)) {
                localStringBuilder.append(this.rawType.getName().replace(((ParameterizedTypeImpl) this.ownerType).rawType.getName() + "$", ""));
            } else {
                localStringBuilder.append(this.rawType.getName());
            }
        } else {
            localStringBuilder.append(this.rawType.getName());
        }
        if ((this.actualTypeArguments != null) && (this.actualTypeArguments.length > 0)) {
            localStringBuilder.append("<");
            int i = 1;
            for (Type localType : this.actualTypeArguments) {
                if (i == 0) {
                    localStringBuilder.append(", ");
                }
                if ((localType instanceof Class<?>)) {
                    localStringBuilder.append(((Class<?>) localType).getName());
                } else {
                    // if(null!=localType){
                    localStringBuilder.append(localType.toString());
                    // }
                }

                i = 0;
            }
            localStringBuilder.append(">");
        }

        return localStringBuilder.toString();
    }

}
