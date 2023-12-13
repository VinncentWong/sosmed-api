package centwong.twitter.configuration;

import centwong.twitter.entity.Response;
import centwong.twitter.security.filter.JwtFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfiguration {

    @Value("${app.username}")
    private String appUsername;

    @Value("${app.password}")
    private String appPassword;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public SecurityWebFilterChain filterChain(ServerHttpSecurity req){
        return req
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAfter(this.jwtFilter(), SecurityWebFiltersOrder.EXCEPTION_TRANSLATION)
                .exceptionHandling((c) -> {
                    c.accessDeniedHandler((server, exception) -> {
                        var response = server.getResponse();
                        return response
                                .writeWith(
                                        Mono.defer(() -> {
                                            var dataResponse = new Response<>(false, exception.getMessage(), null, null);
                                            try {
                                                return Mono.just(
                                                        new DefaultDataBufferFactory().wrap(
                                                                this.objectMapper
                                                                        .writeValueAsBytes(dataResponse)
                                                        )
                                                );
                                            } catch (JsonProcessingException e) {
                                                return Mono.error(e);
                                            }
                                        })
                                );
                    });
                })
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(){
        var user = User
                .builder()
                .username(this.appUsername)
                .password(this.appPassword)
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public BCryptPasswordEncoder bcrpytEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }
}
