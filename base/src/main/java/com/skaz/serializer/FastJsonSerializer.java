package com.skaz.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.skaz.Constants;
import lombok.NoArgsConstructor;

/**
 * @author jungle
 */
@NoArgsConstructor
public class FastJsonSerializer implements Serializer {

    public static final SerializerFeature[] FEATURES = {
            SerializerFeature.SkipTransientField
            , SerializerFeature.PrettyFormat
            , SerializerFeature.WriteMapNullValue
            , SerializerFeature.DisableCircularReferenceDetect
            , SerializerFeature.WriteClassName
            , SerializerFeature.WriteDateUseDateFormat};

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        String json = JSON.toJSONString(object, FEATURES);
        return json.getBytes(Constants.CHARSET);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return parse(bytes);
    }

    private <T> T parse(byte[] bytes) {
        String json = new String(bytes, Constants.CHARSET);
        if (null == json) {
            return null;
        }
        return (T) JSON.parse(json);
    }
}
