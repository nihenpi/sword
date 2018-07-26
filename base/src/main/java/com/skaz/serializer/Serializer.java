package com.skaz.serializer;

/**
 * @author jungle
 */
public interface Serializer {
    byte[] serialize(Object object);

    Object deserialize(byte[] bytes);

}
