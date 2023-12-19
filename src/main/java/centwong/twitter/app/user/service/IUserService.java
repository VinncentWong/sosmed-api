package centwong.twitter.app.user.service;

import centwong.twitter.dto.UserDto;
import centwong.twitter.entity.PgParam;
import centwong.twitter.entity.User;
import centwong.twitter.entity.UserParam;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IUserService {
    Mono<User> create(UserDto.Create dto);
    Mono<User> login(UserDto.Login dto);
    Mono<User> get(Long id);
    Mono<List<User>> getList(UserParam param);

    Mono<User> delete(Long id);
}
