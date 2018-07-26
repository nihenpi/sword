package com.skaz;

import com.skaz.serializer.FastJsonSerializer;
import com.skaz.serializer.StringSerializer;
import com.skaz.utils.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.skaz.jedis.JedisTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author jungle
 */
@Configuration
public class JedisConfig {

    @Value("${app.redis.host:127.0.0.1}")
    private String host;

    @Value("${app.redis.port:6379}")
    private Integer port;

    @Value("${app.redis.timeout:3000}")
    private Integer timeout;

    @Value("${app.redis.pass}")
    private String pass;

    @Value("${app.redis.maxTotal:1024}")
    private Integer maxTotal;

    @Value("${app.redis.minIdle:256}")
    private Integer maxIdle;

    @Value("${app.redis.maxWaitMillis:3000}")
    private Long maxWaitMillis;


    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(this.maxTotal);
        config.setMaxIdle(this.maxIdle);
        config.setMaxWaitMillis(this.maxWaitMillis);
        return config;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
        if (Strings.isNotNullOrEmpty(this.pass)) {
            return new JedisPool(jedisPoolConfig, this.host, this.port, this.timeout, this.pass);
        }
        return new JedisPool(jedisPoolConfig, this.host, this.port, this.timeout);
    }

    @Bean
    public JedisTemplate jedisTemplate(JedisPool jedisPool) {
        JedisTemplate jedisTemplate = new JedisTemplate(jedisPool);
        jedisTemplate.setKeySerializer(new StringSerializer());
        jedisTemplate.setValueSerializer(new FastJsonSerializer());
        return jedisTemplate;
    }
}
