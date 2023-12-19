package centwong.twitter.app.user.repository;

import centwong.twitter.entity.User;
import centwong.twitter.entity.UserParam;
import centwong.twitter.util.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
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
                .collectList();
    }
}
