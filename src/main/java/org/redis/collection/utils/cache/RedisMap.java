package org.redis.collection.utils.cache;

import org.redis.collection.configuration.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.*;
import java.util.function.BiFunction;

public class RedisMap implements Map<String, Integer> {

    private final Jedis jedis;
    private final String redisKey;

    public RedisMap(final RedisConfiguration config, final String redisKey) {
        this.jedis = config.getJedis();
        this.redisKey = redisKey;
    }

    @Override
    public int size() {
        return ((int) jedis.hlen(redisKey));
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(final Object key) {
        try {
            final String keyString = String.valueOf(key);
            return jedis.hexists(redisKey, keyString);
        } catch (final ClassCastException e) {
            throw new UnsupportedOperationException("key is not a String");
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        return values().contains(value);
    }

    @Override
    public Integer get(final Object key) {
        try {
            final String value = jedis.hget(redisKey, String.valueOf(key));
            return value == null ? null : Integer.parseInt(value);
        } catch (final ClassCastException e) {
            throw new UnsupportedOperationException("key is not a String or value is not an Integer");
        }
    }


    @Override
    public Integer put(final String key, final Integer value) {
        jedis.hset(redisKey, key, String.valueOf(value));
        return value;
    }

    @Override
    public Integer remove(final Object key) {
        final Integer value = get(key);
        jedis.hdel(redisKey, String.valueOf(key));
        return value;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Integer> m) {
        final Map<String, String> map = new HashMap<>();
        for (Entry<? extends String, ? extends Integer> entry : m.entrySet()) {
            map.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        jedis.hmset(redisKey, map);

    }

    @Override
    public void clear() {
        jedis.del(redisKey);
    }

    @Override
    public Collection<Integer> values() {
        final List<String> values = jedis.hvals(redisKey);
        final List<Integer> intValues = new ArrayList<>();
        for (String value : values) {
            try {
                intValues.add(Integer.parseInt(value));
            } catch (final ClassCastException e) {
                throw new UnsupportedOperationException("value is not an Integer");
            }
        }
        return intValues;
    }


    @Override
    public Set<Entry<String, Integer>> entrySet() {
        return scanAndConvertEntries((entryKey, entryValue) -> new AbstractMap.SimpleEntry<>(entryKey, Integer.parseInt(entryValue)));
    }

    @Override
    public Set<String> keySet() {
        return scanAndConvertEntries((entryKey, entryValue) -> entryKey);
    }

    private <T> Set<T> scanAndConvertEntries(BiFunction<String, String, T> converter) {
        Set<T> resultSet = new HashSet<>();
        String cursor = ScanParams.SCAN_POINTER_START;

        do {
            ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(redisKey, cursor);
            for (Map.Entry<String, String> entry : scanResult.getResult()) {
                T convertedEntry = converter.apply(entry.getKey(), entry.getValue());
                resultSet.add(convertedEntry);
            }
            cursor = scanResult.getCursor();
        } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

        return resultSet;
    }
}
