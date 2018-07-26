package com.skaz.serializer;

import com.skaz.Constants;
import lombok.NoArgsConstructor;

/**
 * @author jungle
 */
@NoArgsConstructor
public class StringSerializer implements Serializer {
    @Override
    public byte[] serialize(Object string) {
        return string == null ? null : (String.valueOf(string).getBytes(Constants.CHARSET));
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return bytes == null ? null : (new String(bytes, Constants.CHARSET));
    }
}
