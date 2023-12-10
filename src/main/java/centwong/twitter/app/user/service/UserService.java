package centwong.twitter.app.user.service;

import centwong.twitter.app.redis.IRedisRepository;
import centwong.twitter.app.redis.RedisRepository;
import centwong.twitter.entity.constant.UserConstant;
import centwong.twitter.app.user.repository.UserRepository;
import centwong.twitter.dto.UserDto;
import centwong.twitter.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Service
@Transactional
public class UserService implements IUserService{

    private final UserRepository repository;

    private final IRedisRepository redisRepository;

    private final ReactiveElasticsearchOperations elasticRepository;

    @Autowired
    public UserService(UserRepository repository, RedisRepository redisRepository, ReactiveElasticsearchOperations elasticRepository){
        this.repository = repository;
        this.redisRepository = redisRepository;
        this.elasticRepository = elasticRepository;
    }

    @Override
    public Mono<User> create(UserDto.Create dto) {
        var user = dto.toUser();
        var insertUser = this
                .repository
                .save(user)
                .flatMap(elasticRepository::save);
        var deleteCache = this
                .redisRepository
                .deleteCache(UserConstant.ALL);
        return insertUser
                .zipWith(deleteCache)
                .map(Tuple2::getT1)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
