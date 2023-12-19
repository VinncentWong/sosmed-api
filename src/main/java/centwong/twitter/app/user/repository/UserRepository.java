package centwong.twitter.app.user.repository;

import centwong.twitter.entity.User;
import centwong.twitter.entity.UserParam;
import centwong.twitter.mapper.UserMapper;
import centwong.twitter.util.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class UserRepository implements IUserRepository{

    private final R2dbcEntityTemplate template;

    @Autowired
    public UserRepository(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<User> save(User user) {
        return this
                .template
                .insert(user);
    }

    @Override
    public Mono<User> get(UserParam param) {
        return this
                .template
                .selectOne(QueryUtils.generateSqlExtension(param), User.class);
    }

    @Override
    public Mono<List<User>> getList(UserParam param) {
        return this
                .template
                .select(QueryUtils.generateSqlExtension(param), User.class)
                .collectList()
                .doOnSuccess((v) -> log.info("success get list user: {}", v));
    }

    @Override
    public Mono<User> update(Long id, User user) {
        var userParam = UserParam
                .builder()
                .id(id)
                .build();
        return this
                .get(userParam)
                .flatMap((u) -> {
                    var newUser = UserMapper.INSTANCE.updateUser(user, u);
                    return this.template.update(newUser);
                });
    }
}
