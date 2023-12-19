package centwong.twitter.app.redis;

import centwong.twitter.entity.PgParam;
import centwong.twitter.util.ObjectMapperUtil;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class RedisRepository implements IRedisRepository{

    @Autowired
    private RedisReactiveCommands<String, String> commands;

    @Autowired
    private ObjectMapperUtil mapper;

    @Override
    public Mono<String> upsertCache(String key, Object dto, Object data, Duration duration) {
        return this.commands
                .set(String.format(key, this.mapper.writeValueAsString(dto)), this.mapper.writeValueAsString(data), new SetArgs().ex(duration));
    }

    @Override
    public Mono<Long> deleteCache(String key) {
        return this
                .commands
                .keys(key)
                .collectList()
                .flatMap((keys) ->
                        this.commands.del(String.valueOf(keys))
                                .doOnNext((d) -> log.info("removing key {}", String.valueOf(keys)))
                );
    }

    @Override
    public <T> Mono<T> get(String key, Long id, Class<T> clazz) {
        return this
                .commands
                .get(String.format(key, id))
                .map((s) -> this.mapper.readValue(s, clazz));
    }

    @Override
    public <T> Mono<List<T>> getList(String key, Object data) {
        return this
                .commands
                .get(String.format(key, this.mapper.writeValueAsString(data)))
                .map((s) -> this.mapper.readListValue(s));
    }
}
