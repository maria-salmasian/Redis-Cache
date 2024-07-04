package org.redis.collection.configuration;

import redis.clients.jedis.Jedis;

public class RedisConfiguration {
    private Jedis jedis;

    public void establishConnection(String hostname, int port) {
        try {
            jedis = new Jedis(hostname, port);
            jedis.connect();
            if (!jedis.isConnected()) {
                System.out.println("Jedis Connection Exception");
            } else {
                System.out.println("Connection is successfully established");
            }
        } catch (Exception e) {
            System.out.println("Jedis Connection Exception: " + e.getMessage());
        }
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void close() {
        jedis.quit();
        jedis.close();
    }
}