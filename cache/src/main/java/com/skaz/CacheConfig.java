package com.skaz;

import com.skaz.cache.CacheTemplate;
import com.skaz.jedis.JedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jungle
 */
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    @Autowired
    private CacheProperties cacheProperties;

    @Bean
    public CacheTemplate cacheTemplate(JedisTemplate jedisTemplate) {
        CacheTemplate cacheTemplate = new CacheTemplate();
        cacheTemplate.setLocalEnabled(cacheProperties.isLocalEnabled());
        cacheTemplate.setRemoteEnabled(cacheProperties.isRemoteEnabled());
        cacheTemplate.setSetCmdEnabled(cacheProperties.isSetCmdEnabled());
        cacheTemplate.setLocalStoreLocation(cacheProperties.getLocalStoreLocation());
        cacheTemplate.setLocalMaxBytesLocalHeap(cacheProperties.getLocalMaxBytesLocalHeap());
        cacheTemplate.setLocalMaxBytesLocalDisk(cacheProperties.getLocalMaxBytesLocalDisk());
        cacheTemplate.setLocalTimeToLiveSeconds(cacheProperties.getLocalTimeToLiveSeconds());
        cacheTemplate.setLocalDiskExpiryThreadIntervalSeconds(cacheProperties.getLocalDiskExpiryThreadIntervalSeconds());
        cacheTemplate.setFetchTimeoutSeconds(cacheProperties.getFetchTimeoutSeconds());
        cacheTemplate.setJedisTemplate(jedisTemplate);
        return cacheTemplate;
    }
}
