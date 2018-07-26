package com.skaz;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存相关配置
 *
 * @author jungle
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    /**
     * 是否启用本地缓存
     */
    private boolean localEnabled = true;

    /**
     * 是否启用远程缓存
     */
    private boolean remoteEnabled = false;
    /**
     * 是否开启set同步命令
     */
    private boolean setCmdEnabled = false;

    /**
     * 本地缓存存储磁盘位置
     */
    private String localStoreLocation = "/tmp/cache";
    /**
     * 本地缓存最大内存大小
     */
    private String localMaxBytesLocalHeap = "256M";
    /**
     * 本地缓存最大磁盘大小
     */
    private String localMaxBytesLocalDisk = "1024M";

    /**
     * 本地缓存30分钟过期
     */
    private int localTimeToLiveSeconds = 30 * 60;
    /**
     * 不使用timeToIdle
     */
    private final int localTimeToIdleSeconds = 0;

    /**
     * 本地缓存15分钟清理一次
     */
    private int localDiskExpiryThreadIntervalSeconds = 15 * 60;
    /**
     * fetch命令最长等待3秒
     */
    private int fetchTimeoutSeconds = 3;


}
