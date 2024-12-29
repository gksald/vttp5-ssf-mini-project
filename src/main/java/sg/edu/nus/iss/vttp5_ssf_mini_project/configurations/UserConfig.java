package sg.edu.nus.iss.vttp5_ssf_mini_project.configurations;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import sg.edu.nus.iss.vttp5_ssf_mini_project.utilities.Utility;

@Configuration
public class UserConfig {
    
    // Created for logging purposes in this AppConfig class
  private Logger logger = Logger.getLogger(UserConfig.class.getName());

  // Values of properties are injected from resources/application.properties into the configuration

  // Railway: SPRING_REDIS_HOST
  @Value("${spring.data.redis.host}")
  private String redisHost;

  // Railway: SPRING_REDIS_PORT
  @Value("${spring.data.redis.port}")
  private Integer redisPort;

  // Railway: SPRING_REDIS_USERNAME
  @Value("${spring.data.redis.username}")
  private String redisUsername;

  // Railway: SPRING_REDIS_PASSWORD
  @Value("${spring.data.redis.password}")
  private String redisPassword;

  // Railway: SPRING_REDIS_DATABASE
  @Value("${spring.data.redis.database}")
  private Integer redisDatabase;

  // This method is annotated with @Bean, indicating that it produces a bean that can be managed by the Spring container.
  @Bean(Utility.BEAN_REDIS)
  public RedisTemplate<String, String> createRedisConnection() {

    // Create a redis configuration
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

    // Configuration values are injected from application.properties file
    config.setHostName(redisHost);
    config.setPort(redisPort);
    config.setDatabase(redisDatabase);

    // Only set the username and password if they are not null and not empty
    if (redisUsername != null && !redisUsername.isEmpty()) {
      config.setUsername(redisUsername);
    }
    if (redisPassword != null && !redisPassword.isEmpty()) {
      config.setPassword(redisPassword);
    }

    // Logging statements are used to log information about the Redis configuration
    logger.log(Level.INFO,
        "Using Redis database %d".formatted(redisDatabase));
    logger.log(Level.INFO,
        "Using Redis username is set: %b".formatted(redisUsername != null && !redisUsername.isEmpty()));
    logger.log(Level.INFO,
        "Using Redis password is set: %b".formatted(redisPassword != null && !redisPassword.isEmpty()));

    // Create the client and factory
    JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();
    JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
    jedisFac.afterPropertiesSet();

    // Create the template with the client
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisFac);

    // Keys => set in UTF-8
    // Values => optional value serializer if string values are to be saved as UTF-8
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());

    // Return RedisTemplate bean
    return template;
  }

}
