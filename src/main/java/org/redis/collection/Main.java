package org.redis.collection;

import org.redis.collection.configuration.RedisConfiguration;
import org.redis.collection.utils.cache.RedisMap;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final RedisConfiguration config = new RedisConfiguration();
        config.establishConnection("localhost", 6379);

        final Map<String, Integer> redisMap = new RedisMap(config, "redisMap");

        final Map<String, Integer> values = new HashMap<>();

        redisMap.put("apple", 1);

        values.put("banana", 2);
        values.put("orange", 3);
        redisMap.putAll(values);

        System.out.println("Size of redisMap: " + redisMap.size());

        System.out.println("Value for apple: " + redisMap.get("apple"));
        System.out.println("Value for banana: " + redisMap.get("banana"));
        System.out.println("Value for orange: " + redisMap.get("orange"));

        System.out.println("EntrySet: " + redisMap.entrySet());
        System.out.println("KeySet: " + redisMap.keySet());
        System.out.println("Values: " + redisMap.values());

        redisMap.remove("banana");
        System.out.println("Value for banana after removing it: " + redisMap.get("banana"));
        System.out.println("Banana is contained: " + redisMap.containsKey("banana"));
        System.out.println("Orange is contained: " + redisMap.containsKey("orange"));
        System.out.println("4 is contained: " + redisMap.containsValue(4));
        System.out.println("3 is contained: " + redisMap.containsValue(3));

        redisMap.clear();
        System.out.println("Size of redisMap after clear(): " + redisMap.size());

        config.close();
    }
}