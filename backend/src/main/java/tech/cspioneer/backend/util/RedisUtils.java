package tech.cspioneer.backend.util;

import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.util.Pool;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Component
public class RedisUtils {
    
    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private int redisPort;
    
    @Value("${spring.data.redis.password}")
    private String redisPassword;
    
    @Value("${spring.data.redis.timeout:2000}")
    private int redisTimeout;
    
    private static String server_address;
    private static int server_port;
    private static String server_password;
    private static int timeout;
    
    private static Pool<Jedis> jedisPool;
    
    // 用于标记Redis服务是否可用
    private static boolean redisAvailable = false;
    
    @PostConstruct
    public void init() {
        System.out.println("初始化Redis连接...");
        server_address = redisHost;
        server_port = redisPort;
        server_password = redisPassword;
        timeout = redisTimeout;
        
        System.out.println("Redis连接信息: " + server_address + ":" + server_port + 
                ", 超时: " + timeout + "毫秒, 密码长度: " + 
                (server_password != null ? (server_password.isEmpty() ? "0" : "有值") : "null"));
        
        try {
            // 初始化连接池
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(8);
            config.setMaxIdle(8);
            config.setMinIdle(0);
            config.setMaxWait(java.time.Duration.ofMillis(-1));
            
            // 使用127.0.0.1而不是localhost，避免DNS解析问题
            jedisPool = new JedisPool(config, "127.0.0.1", server_port, timeout);
            
            // 测试连接
            try (Jedis jedis = jedisPool.getResource()) {
                String pingResponse = jedis.ping();
                System.out.println("Redis连接测试成功: " + pingResponse);
                redisAvailable = true;
            }
        } catch (Exception e) {
            System.err.println("Redis连接池初始化失败: " + e.getMessage());
            e.printStackTrace();
            redisAvailable = false;
            // 不抛出异常，让应用继续启动，但标记Redis不可用
        }
    }

    private RedisUtils() {} // Private constructor to prevent instantiation

    // 检查Redis是否可用
    public static boolean isRedisAvailable() {
        return redisAvailable && jedisPool != null;
    }

    private static Jedis getJedis(int dbIndex) {
        if (jedisPool == null) {
            throw new IllegalStateException("Redis连接池未初始化，请确保Redis服务已启动并正确配置");
        }
        
        try {
            Jedis jedis = jedisPool.getResource();
            jedis.select(dbIndex);
            return jedis;
        } catch (Exception e) {
            throw new RuntimeException("获取Redis连接失败: " + e.getMessage(), e);
        }
    }

    private static JReJSON getJReJSON() {
        // GetJReJSON doesn't need Jedis authentication or selection as it uses the JedisPool directly
        return new JReJSON(jedisPool);
    }

    // 设置/修改一个键值对,并设置过期时间
    public static void set(String key, String value, int seconds, int dbIndex) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("键和值不能为空");
        }
        
        if (!isRedisAvailable()) {
            throw new RuntimeException("Redis服务不可用");
        }
        
        try (Jedis jedis = getJedis(dbIndex)) {
            String result = jedis.set(key, value);
            if (!"OK".equals(result)) {
                throw new RuntimeException("Redis SET操作返回非OK结果: " + result);
            }
            
            Long expireResult = jedis.expire(key, seconds);
            if (expireResult != 1L) {
                System.out.println("Redis EXPIRE操作可能未成功，返回结果: " + expireResult);
            }
        } catch (Exception e) {
            System.err.println("Redis SET操作失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Redis写入失败: " + e.getMessage(), e);
        }
    }

    // 获取键值对
    public static String get(String key, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            return jedis.get(key);
        }
    }

    // 删除键值对
    public static void del(String key, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            jedis.del(key);
        }
    }

    // 检查键是否存在
    public static boolean exists(String key, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            return jedis.exists(key);
        }
    }

    // 返回所有键值对
    public static String[] keys(int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            return jedis.keys("*").toArray(new String[0]);
        }
    }

    // 使用 RedisJSON 设置 JSON 数据
    public static void jsonSet(String key, String path, Object value, int dbIndex) {
        JReJSON jsonApi = getJReJSON();
        try (Jedis jedis = getJedis(dbIndex)) {
            jsonApi.set(key, value, new Path(path));
        }
    }

    // 使用 RedisJSON 获取 JSON 数据
    public static <T> T jsonGet(String key, String path, Class<T> clazz, int dbIndex) {
        JReJSON jsonApi = getJReJSON();
        try (Jedis jedis = getJedis(dbIndex)) {
            return jsonApi.get(key, clazz, new Path(path));
        }
    }

    // 使用 RedisJSON 删除 JSON 数据
    public static void jsonDel(String key, String path, int dbIndex) {
        JReJSON jsonApi = getJReJSON();
        try (Jedis jedis = getJedis(dbIndex)) {
            jsonApi.del(key, new Path(path));
        }
    }

    // 设置/修改一个 Hash 键值对
    public static void hset(String key, String field, String value, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            jedis.hset(key, field, value);
        }
    }

    // 获取一个 Hash 键值对
    public static String hget(String key, String field, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            return jedis.hget(key, field);
        }
    }

    // 获取一个 Hash 的所有键值对
    public static Map<String, String> hgetAll(String key, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            return jedis.hgetAll(key);
        }
    }

    // 删除一个 Hash 键值对
    public static void hdel(String key, String field, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            jedis.hdel(key, field);
        }
    }

    // 删除指定用户名的键值对
    public static void deleteByValue(String expectedUsername, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            String cursor = "0";
            ScanParams scanParams = new ScanParams().match("*").count(100);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();

                for (String key : scanResult.getResult()) {
                    String value = jedis.get(key);
                    if (expectedUsername.equals(value)) {
                        jedis.del(key);
                        System.out.println("Deleted key: " + key + " with value: " + value);
                    }
                }
            } while (!"0".equals(cursor));
        }
    }

    // 添加 token 到黑名单
    public static void addToBlacklist(String token, int seconds, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            jedis.set(token, "blacklisted");
            jedis.expire(token, seconds);
        }
    }

    // 检查 token 是否在黑名单中
    public static boolean isTokenBlacklisted(String token, int dbIndex) {
        try (Jedis jedis = getJedis(dbIndex)) {
            return jedis.exists(token);
        }
    }
}