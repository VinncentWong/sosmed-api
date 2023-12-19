package centwong.twitter.app.user.repository;

import centwong.twitter.entity.User;
import centwong.twitter.entity.UserParam;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IUserRepository {

    String get = "SELECT * FROM `user` ";

    Mono<User> save(User user);

    Mono<User> get(UserParam param);

    Mono<List<User>> getList(UserParam param);
}
