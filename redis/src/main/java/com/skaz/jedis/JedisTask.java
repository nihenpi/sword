package com.skaz.jedis;

import redis.clients.jedis.Jedis;

/**
 * @author jungle
 */
public interface JedisTask<T> {

    /**
     * 通过传入的jedis客户端，执行指定的操作
     *
     * @param jedis
     * @return
     */
    T run(Jedis jedis);
}
