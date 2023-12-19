package centwong.twitter.app.redis;

import centwong.twitter.entity.PgParam;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public interface IRedisRepository {
    Mono<String> upsertCache(String key, Object dto, Object data, Duration duration);
    Mono<Long> deleteCache(String key);
    <T> Mono<T> get(String key, Long id, Class<T> clazz);
    <T> Mono<List<T>> getList(String key, Object data);
}
