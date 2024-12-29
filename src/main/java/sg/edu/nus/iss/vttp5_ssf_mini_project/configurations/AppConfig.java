// package sg.edu.nus.iss.vttp5_ssf_mini_project.configurations;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
// import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
// import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.serializer.StringRedisSerializer;

// @Configuration
// public class AppConfig {
    
//     @Value("${spring.data.redis.host}")
//     private String redisHost;

//     @Value("${spring.data.redis.port}")
//     private Integer redisPort;

//     @Value("${spring.data.redis.database}")
//     private Integer redisNumber;

//     @Value("${spring.data.redis.username}")
//     private String redisUsername;

//     @Value("${spring.data.redis.password}")
//     private String redisPassword;

//     @Bean
//     public RedisTemplate<String, Object> createRedisTemplate() {
        
//         // Set up standalone Redis configuration
//         final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
//         config.setDatabase(redisNumber);
//         if (!redisUsername.trim().equals("")) {
//             config.setUsername(redisUsername);
//             config.setPassword(redisPassword);
//         }
        
//         final JedisClientConfiguration jedisClient = JedisClientConfiguration
//             .builder().build();

//         final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
//         jedisFac.afterPropertiesSet();

//         final RedisTemplate<String, Object> template = new RedisTemplate<>();
//         template.setConnectionFactory(jedisFac);
//         template.setKeySerializer(new StringRedisSerializer());
//         template.setValueSerializer(new StringRedisSerializer());
//         template.setHashKeySerializer(new StringRedisSerializer());
//         template.setHashValueSerializer(new StringRedisSerializer());

//         return template;

//     }

// }
