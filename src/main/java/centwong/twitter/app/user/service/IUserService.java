package centwong.twitter.app.user.service;

import centwong.twitter.dto.UserDto;
import centwong.twitter.entity.User;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> create(UserDto.Create dto);
}
