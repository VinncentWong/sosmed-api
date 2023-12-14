package centwong.twitter.util;

import centwong.twitter.entity.AuthDto;
import centwong.twitter.entity.constant.SecurityConstant;
import centwong.twitter.security.authentication.JwtAuthentication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private ObjectMapper mapper;

    @Value("${app.secret_key}")
    private String secretKey;

    @SneakyThrows
    public String generateToken(AuthDto dto){
        var claims = Jwts
                .claims()
                .setId(String.valueOf(dto.getId()))
                .setSubject(dto.getPrincipal());
        claims.put(SecurityConstant.CREATED_AT, dto.getCreatedAt());
        claims.put(SecurityConstant.ROLES, mapper.writeValueAsString(dto.getRoles()));
        return Jwts
                .builder()
                .serializeToJsonWith(new JacksonSerializer<>(this.mapper))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(this.secretKey.getBytes()))
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts
                    .parserBuilder()
                    .setSigningKey(Base64.getEncoder().encode(this.secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch(Exception ex){
            return false;
        }
    }

    public Authentication getAuthentication(String token){
        try{
            var claims = Jwts
                    .parserBuilder()
                    .setSigningKey(Base64.getEncoder().encode(this.secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            var rolesStr = (String)claims
                    .get(SecurityConstant.ROLES);
            List<SimpleGrantedAuthority> roles;
            if(rolesStr == null){
                roles = List.of();
            } else {
                roles = this
                        .mapper
                        .readValue(rolesStr, new TypeReference<List<String>>() {})
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            return new JwtAuthentication(claims.getSubject(), null, roles);
        } catch(Exception ex){
            return new JwtAuthentication(null, null);
        }
    }
}
