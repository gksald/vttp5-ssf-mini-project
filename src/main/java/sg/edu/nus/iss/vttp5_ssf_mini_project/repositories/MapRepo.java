// package sg.edu.nus.iss.vttp5_ssf_mini_project.repositories;

// import java.time.Duration;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Repository;

// @Repository
// public class MapRepo {
//     @Autowired
//     RedisTemplate<String, Object> redisTemplate;

//     public void saveEntry(String redisKey, String hashKey, String hashValue) {
//         redisTemplate.opsForHash().put(redisKey, hashKey, hashValue);
//     }

//     // day 15 - slide 36
//     public void create(String redisKey, String hashKey, String hashValue) {
//         redisTemplate.opsForHash().put(redisKey, hashKey, hashValue);
//     }
//     // day 15 - slide 37
//     public Object get(String redisKey, String hashKey) {
//         return redisTemplate.opsForHash().get(redisKey, hashKey);
//     }
//     // day 15 - slide 38
//     public Long delete(String redisKey, String hashKey) {
//         return redisTemplate.opsForHash().delete(redisKey, hashKey);
//     }
//     // day 15 - slide 39
//     public Boolean keyExists(String redisKey, String hashKey) {
//         return redisTemplate.opsForHash().hasKey(redisKey, hashKey);
//     }
    
//     // day 15 - slide 40 (twisted)
//     // <Object, Object> = <HashKeys, HaskValues>
//     public Map<Object, Object> getEntries(String redisKey) {
//         return redisTemplate.opsForHash().entries(redisKey);
//     }

//     // day 15 - slide 40
//     public Set<Object> getKeys(String redisKey) {
//         return redisTemplate.opsForHash().keys(redisKey);
//     }

//     public List<Object> getValues(String redisKey) {
//         return redisTemplate.opsForHash().values(redisKey);
//     }

//     // day 15 - slide 41
//     public Long size(String redisKey) {
//         return redisTemplate.opsForHash().size(redisKey);
//     }

//     public void expire(String redisKey, Long expireValue) {
//         Duration expireDuration = Duration.ofSeconds(expireValue);
//         redisTemplate.expire(redisKey, expireDuration);
//     }
// }
