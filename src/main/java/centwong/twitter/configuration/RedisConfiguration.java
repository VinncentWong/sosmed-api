package centwong.twitter.configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private Integer port;

    @Value("${redis.password}")
    private String password;

    private RedisClient client;

    @Bean
    public RedisClient redisClient(){
        this.client =  RedisClient
                .create(
                        RedisURI
                                .builder()
                                .withHost(this.host)
                                .withPort(this.port)
                                .withPassword(this.password)
                                .build()
                );
        return this.client;
    }

    @Bean
    public RedisReactiveCommands<String, String> reactiveCommands(){
        return this
                .redisClient()
                .connect()
                .reactive();
    }

    @PreDestroy
    public void close(){
        if(this.client != null){
            this
                .client
                .close();
        }
    }
}
