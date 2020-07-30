package com.xyz.cache;

import com.google.common.collect.Maps;
import org.redisson.api.RedissonClient;

import java.util.Map;

public interface CacheManager {
    Map<String, ICache> localRepo = Maps.newConcurrentMap();
    Map<String, ICache> redisRepo = Maps.newConcurrentMap();

    static <K, V> ICache<K, V> getLocalCache(String namespace) {
        @SuppressWarnings("unchecked")
        ICache<K, V> cache = localRepo.get(namespace);
        if (cache == null) {
            cache = new LocalCache<>();
            ICache previousCache = localRepo.putIfAbsent(namespace, cache);
            if (previousCache != null) {
                cache = previousCache;
            }
        }
        return cache;
    }

    static <K, V> ICache<K, V> getRedisCache(String namespace, RedissonClient redissonClient) {
        @SuppressWarnings("unchecked")
        ICache<K, V> cache = redisRepo.get(namespace);
        if (cache == null) {
            cache = new RedisCache<>(redissonClient, namespace);
            ICache previousCache = redisRepo.putIfAbsent(namespace, cache);
            if (previousCache != null) {
                cache = previousCache;
            }
        }
        return cache;
    }
}
