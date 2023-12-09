package centwong.twitter.app.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class RedisRepository implements IRedisRepository{

    @Autowired
    private RedisReactiveCommands<String, String> commands;

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    public Mono<String> upsertCache(String key, Object data, Duration duration){
        return this.commands
                .set(key, this.mapper.writeValueAsString(data), new SetArgs().ex(duration));
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
}
