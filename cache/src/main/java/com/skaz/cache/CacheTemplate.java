package com.skaz.cache;

import com.google.common.base.Optional;
import com.skaz.jedis.JedisTemplate;
import lombok.Data;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.skaz.cache.Cache.Level;

/**
 * @author jungle
 */
@Data
public class CacheTemplate implements InitializingBean {


    public static final Logger logger = LoggerFactory.getLogger(CacheTemplate.class);

    private boolean localEnabled = true;
    private boolean remoteEnabled = false;
    private boolean setCmdEnabled = false;

    private String localStoreLocation;
    private String localMaxBytesLocalHeap;
    private String localMaxBytesLocalDisk;

    private int localTimeToLiveSeconds;
    private final int localTimeToIdleSeconds = 0;
    private int localDiskExpiryThreadIntervalSeconds;
    private int fetchTimeoutSeconds;

    private net.sf.ehcache.CacheManager cacheManager;
    private final ConcurrentHashMap<String, Future<Ehcache>> ehcaches = new ConcurrentHashMap<>();

    private JedisTemplate jedisTemplate;

    private RedisPubSubSync redisSync;

    @Override
    public void afterPropertiesSet() {

        if (this.localEnabled) {
            Configuration configuration = new Configuration();
            CacheConfiguration defaultCacheConfiguration = new CacheConfiguration();
            configuration.setMaxBytesLocalHeap(localMaxBytesLocalHeap);
            configuration.setMaxBytesLocalDisk(localMaxBytesLocalDisk);
            defaultCacheConfiguration.setEternal(false);
            defaultCacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
            defaultCacheConfiguration.setDiskExpiryThreadIntervalSeconds(localDiskExpiryThreadIntervalSeconds);
            configuration.setDefaultCacheConfiguration(defaultCacheConfiguration);
            this.cacheManager = new CacheManager(configuration);
        }
    }

    /**
     * 设置缓存
     */
    public void set(String name, String key, Object value) {
        this.set(name, key, value, Level.Remote);
        if (localEnabled) {
            this.set(name, key, value, Level.Local);
        }
    }

    /**
     * 设置缓存(根据缓存层级)
     */
    protected void set(String name, String key, Object value, Level level) {
        if (level.equals(Level.Local)) {
            if (!localEnabled) {
                return;
            }
            this.getEhcache(name).put(new Element(key, value, false, localTimeToIdleSeconds, localTimeToLiveSeconds));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("set > name:" + name + ",key:" + key + ",local.ttl:" + this.ttl(name, key, Level.Local) + ",remote.ttl:" + this.ttl(name, key, Level.Remote) + ",level:" + level);
        }
    }

    /**
     * 设置缓存与过期时间
     */
    public void set(String name, String key, Object value, int ttl) {
        if (remoteEnabled) {
            this.set(name, key, value, ttl, Level.Remote);
            //发送消息，让集群中的其他应用缓存到本地
            this.sendSetCmd(name, key);
        }
        if (localEnabled) {
            this.set(name, key, value, ttl, Level.Local);
        }
    }

    /**
     * 发送新增了缓存的消息，消费者接受到消息之后，会去重新获取最新的缓存
     *
     * @param name
     * @param key
     */
    private void sendSetCmd(String name, String key) {
        if (localEnabled) {
            Command command = Command.set(name, key);
            redisSync.sendCommand(command);
            if (logger.isDebugEnabled()) {
                logger.debug("sendSetCmd > " + "name:" + name + ",key:" + key);
            }
        }
    }

    /**
     * 设置缓存与过期时间(根据缓存层级)
     */
    protected void set(String name, String key, Object value, int ttl, Level level) {
        if (level.equals(Level.Local)) {
            if (!localEnabled) {
                return;
            }
            //Remote为永久，本地缓存最多存30分钟
            if (ttl == Cache.NO_EXPIRE) {
                this.getEhcache(name).put(new Element(key, value, localTimeToIdleSeconds, localTimeToLiveSeconds));
            } else if (ttl > 0) {
                // 当设置的ttl时间大于本地默认的缓存TTL,则使用本地默认的TTL,即localTimeToIdleSeconds.反之亦然.
                ttl = ttl < localTimeToLiveSeconds ? ttl : localTimeToLiveSeconds;
                this.getEhcache(name).put(new Element(key, value, localTimeToIdleSeconds, ttl));
            }
        }
        //设置Remote缓存
        else {
            this.syncToRedis(name, key, value, ttl, Cache.Operator.SET);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("set > name:" + name + ",key:" + key + ",local.ttl:" + this.ttl(name, key, Level.Local) + ",remote.ttl:" + this.ttl(name, key, Level.Remote) + ",level:" + level);
        }
    }

    private void syncToRedis(String name, String key, Object value, int timeToLiveSeconds, Cache.Operator operator) {
        switch (operator) {
            case SET:
                int ttl = 0;
                ttl = Optional.fromNullable(timeToLiveSeconds).or(ttl);
                if (ttl == 0) {
                    this.jedisTemplate.set(this.getRedisKeyOfElement(name, key), value);
                } else {
                    this.jedisTemplate.set(this.getRedisKeyOfElement(name, key), value, ttl);
                }
                break;
            case CLS:
                break;
            case DEL:
                break;
            case REM:
                break;
            default:
                break;
        }
    }

    /**
     * 若缓存不存在则设置,若存在则返回原缓存值
     */
    public <T> T setIfAbsent(String name, String key, Object value) {
        T existing = this.get(name, key);
        if (existing == null) {
            return existing;
        } else {
            this.set(name, key, value);
            return null;
        }
    }

    /**
     * 若缓存不存在则设置缓存与过期时间,若存在则返回原缓存值
     */
    public <T> T setIfAbsent(String name, String key, Object value, int ttl) {
        T existing = this.get(name, key);
        if (existing == null) {
            return existing;
        } else {
            this.set(name, key, value, ttl);
            return null;
        }
    }

    /**
     * 获取缓存值，如果开启本地缓存，先取本地，本地取不到则取redis中的
     */
    public <T> T get(String name, String key) {
        T value = null;
        if (localEnabled) {
            value = this.get(name, key, Level.Local);
        }
        if (value == null && remoteEnabled) {
            value = this.get(name, key, Level.Remote);
        }
        return value;
    }

    /**
     * 获取缓存值(根据缓存层级)
     */
    public <T> T get(String name, String key, Cache.Level level) {
        T value = null;
        if (level.equals(Level.Local)) {
            if (!localEnabled) {
                return null;
            }
            if (!this.ehcaches.containsKey(name)) {
                return null;
            }
            Element element = this.getEhcache(name).get(key);
            if (logger.isDebugEnabled()) {
                logger.debug("get > name:" + name + ",key:" + key + ",local.ttl:" + this.ttl(name, key, Level.Local) + ",remote.ttl:" + this.ttl(name, key, Level.Remote) + ",level:" + level);
            }
            if (element != null) {
                value = (T) element.getObjectValue();
            }
        } else {
            value = this.jedisTemplate.get(this.getRedisKeyOfElement(name, key));
            if (logger.isDebugEnabled()) {
                logger.debug("get > name:" + name + ",key:" + key + ",local.ttl:" + this.ttl(name, key, Level.Local) + ",remote.ttl:" + this.ttl(name, key, Level.Remote) + ",level:" + level);
            }
            //从远程取到缓存之后还需设置本地缓存
            if (value != null) {
                int ttl = this.ttl(name, key, Level.Remote);
                if (ttl != Cache.NOT_EXIST) {
                    this.set(name, key, value, ttl, Level.Local);
                }
            }
        }
        return value;
    }

    private String getRedisKeyOfElement(String name, String key) {
        return Cache.CACHE_STORE + Cache.SPLITER + name + Cache.SPLITER + key;
    }

    /**
     * 删除单个缓存值
     */
    public void del(String name, String key) {
        this.del(name, key, Level.Local);
        this.del(name, key, Level.Remote);
    }

    /**
     * 删除单个缓存值(根据缓存层级)
     */
    protected void del(String name, String key, Level level) {
        if (level.equals(Level.Local)) {
            if (!localEnabled) {
                return;
            }
            if (this.ehcaches.containsKey(name)) {
                this.getEhcache(name).remove(key);
            }
        }
    }

    /**
     * 删除指定name下所有缓存
     */
    public void rem(String name) {
        this.rem(name, Level.Local);
        this.rem(name, Level.Remote);
    }

    /**
     * 删除指定name下所有缓存(根据缓存层级)
     */
    protected void rem(String name, Level level) {
        if (level.equals(Level.Local)) {
            if (!localEnabled) {
                return;
            }
            if (this.ehcaches.containsKey(name)) {
                this.getEhcache(name).removeAll();
                this.ehcaches.remove(name);
            }
        }
        logger.debug("rem > name:" + name + ",level:" + level);
    }


    /**
     * 获取本地缓存过期剩余时间.单位为秒(根据缓存层级)
     * 0,永久
     * -1,不存在
     */
    public int ttl(String name, String key, Level level) {
        int ttl = -1;
        if (level.equals(Level.Local)) {
            if (!localEnabled) {
                return -1;
            }
            if (this.ehcaches.containsKey(name)) {
                Element element = this.getEhcache(name).get(key);
                if (element != null) {
                    ttl = element.getTimeToLive();
                    if (ttl != 0) {
                        ttl = ttl - (int) ((System.currentTimeMillis() - element.getCreationTime()) / 1000);
                    }
                }
            }
        } else {
            ttl = this.jedisTemplate.ttl(this.getRedisKeyOfElement(name, key)).intValue();
        }
        return ttl;
    }

    /**
     * 判断缓存是否存在(根据缓存层级)
     */
    public boolean exists(String name, String key, Level level) {
        boolean flag = false;
        if (level.equals(Level.Local)) {
            if (this.ehcaches.containsKey(name)) {
                Element element = this.getEhcache(name).get(key);
                flag = element != null;
            }
        }
        return flag;
    }


    /**
     * 关闭
     */
    public void shutdown() {
        this.cacheManager.shutdown();
    }


    /**
     * 创建本地缓存
     */
    private Ehcache getEhcache(final String name) {
        Future<Ehcache> future = this.ehcaches.get(name);
        if (future == null) {
            Callable<Ehcache> callable = new Callable<Ehcache>() {
                @Override
                public Ehcache call() throws Exception {
                    Ehcache cache = cacheManager.getEhcache(name);
                    if (cache == null) {
                        cacheManager.addCache(name);
                        cache = cacheManager.getEhcache(name);
                    }
                    return cache;
                }
            };
            FutureTask<Ehcache> task = new FutureTask<>(callable);
            future = this.ehcaches.putIfAbsent(name, task);
            if (future == null) {
                future = task;
                task.run();
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            this.ehcaches.remove(name);
            throw new CacheException(e);
        }
    }


}
