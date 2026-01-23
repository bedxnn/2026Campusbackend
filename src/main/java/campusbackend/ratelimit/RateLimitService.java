package campusbackend.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean allowRequest(String key){
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket());
        return bucket.tryConsume(1);
    }

    public boolean allowRequest(String key, int maxRequests, Duration duration){
        String bucketKey = key + "_" + maxRequests + "_" + duration.toMinutes();
        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> createBucket(maxRequests, duration));
        return bucket.tryConsume(1);
    }

    private Bucket createBucket(){
        Bandwidth limit = Bandwidth.simple(20, Duration.ofMinutes(10));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createBucket(int maxRequests, Duration duration){
        Bandwidth limit = Bandwidth.simple(maxRequests, duration);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
