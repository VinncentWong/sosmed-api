package centwong.twitter.security.filter;

import centwong.twitter.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@NoArgsConstructor
@AllArgsConstructor
public class JwtFilter implements WebFilter {

    @Autowired
    private JwtUtil util;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var token = request.getHeaders()
                .toSingleValueMap()
                .get(HttpHeaders.AUTHORIZATION);
        if(token == null){
            return chain.filter(exchange);
        }
        if(token.startsWith("Bearer")){
            var extractedToken = token.substring(7);
            if(this.util.validateToken(extractedToken)){
                var auth = this.util.getAuthentication(extractedToken);
                return chain
                        .filter(exchange)
                        .contextWrite((
                                ReactiveSecurityContextHolder.withAuthentication(auth)
                                )
                        );
            } else {
                return Mono.error(new AccessDeniedException("Token jwt tidak valid"));
            }
        } else {
            return Mono.error(new AccessDeniedException("Pastikan token yang dikirim valid"));
        }
    }

}
