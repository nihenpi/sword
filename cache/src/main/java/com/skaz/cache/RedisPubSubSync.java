package com.skaz.cache;

import com.skaz.jedis.JedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import static com.skaz.cache.Cache.CACHE_STORE_SYNC;

/**
 * @author jungle
 */
public class RedisPubSubSync extends JedisPubSub {

    public static final Logger logger = LoggerFactory.getLogger(RedisPubSubSync.class);

    private JedisTemplate jedisTemplate;



    public void sendCommand(Command command){
        jedisTemplate.publish(CACHE_STORE_SYNC,this.jedisTemplate.serializeVal(command));
    }
}
