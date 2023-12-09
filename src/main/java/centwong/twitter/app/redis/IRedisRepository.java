package centwong.twitter.app.redis;

import reactor.core.publisher.Mono;

import java.time.Duration;

public interface IRedisRepository {
    Mono<String> upsertCache(String key, Object data, Duration duration);
    Mono<Long> deleteCache(String key);
}
