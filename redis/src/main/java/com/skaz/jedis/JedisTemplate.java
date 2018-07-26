package com.skaz.jedis;

import com.skaz.serializer.Serializer;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.JedisPool;

/**
 * @author jungle
 */
public class JedisTemplate implements InitializingBean {

    private JedisSupport jedisSupport;
    private JedisPool jedisPool;

    @Setter
    private Serializer keySerializer;

    @Setter
    private Serializer valueSerializer;

    public JedisTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.jedisPool != null && this.jedisSupport == null) {
            this.jedisSupport = new JedisSupport(this.jedisPool);
        }
    }

    public <T> T get(Object key) {
        if (key == null) {
            return null;
        }
        //TODO 集群情况
        return (T) deserializeVal(jedisSupport.get(serializeKey(key)));
    }

    public <T> T deserializeVal(byte[] bytes) {
        return (T) valueSerializer.deserialize(bytes);
    }

    public byte[] serializeKey(Object key) {
        return keySerializer.serialize(key);
    }

    public byte[] serializeVal(Object value) {
        return valueSerializer.serialize(value);
    }

    /**
     * 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以秒为单位，返回 key 的剩余生存时间。
     *
     * @param key
     * @return
     */
    public Long ttl(Object key) {
        if (key == null) {
            return null;
        }
        //TODO 集群
        return jedisSupport.ttl(serializeKey(key));
    }

    public boolean set(String key, Object value) {
        if (key == null || value == null) {
            return false;
        }
        //TODO 集群处理
        return "OK".equals(jedisSupport.set(serializeKey(key), serializeVal(value)));
    }

    public boolean set(String key, Object value, int seconds) {
        if (key == null || value == null) {
            return false;
        }
        //TODO 集群处理
        return "OK".equals(jedisSupport.setex(serializeKey(key), seconds, serializeVal(value)));
    }

    public void publish(String channel, byte[] message) {
        //TODO 集群 同步问题
        jedisSupport.publish(serializeKey(channel), message);
    }
}
