package com.skaz.jedis;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author jungle
 */
@NoArgsConstructor
@AllArgsConstructor
public class JedisSupport {

    private JedisPool jedisPool;

    /**
     * 此方法为通用模板，传一个实现JedisTask的实现类的实例执行自己的任务
     *
     * @param task
     * @param <T>
     * @return
     */
    public <T> T execute(JedisTask<T> task) {
        T result;
        Jedis jedis = jedisPool.getResource();
        try {
            result = task.run(jedis);
        } catch (Exception e) {
            throw e;
        }
        //释放资源
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public byte[] get(final byte[] key) {
        return execute(jedis -> jedis.get(key));
    }

    public long ttl(final byte[] key) {
        return execute(jedis -> jedis.ttl(key));
    }

    public String set(final byte[] key, final byte[] value) {
        return execute(jedis -> jedis.set(key, value));
    }

    public String setex(final byte[] key, final int seconds, final byte[] value) {
        return execute(jedis -> jedis.setex(key, seconds, value));
    }

    public void publish(final byte[] channel, final byte[] message) {
        execute(jedis -> jedis.publish(channel, message));
    }
}
