package centwong.twitter.app.user.service;

import centwong.twitter.app.broker.Producer;
import centwong.twitter.app.redis.IRedisRepository;
import centwong.twitter.app.redis.RedisRepository;
import centwong.twitter.entity.AuthDto;
import centwong.twitter.entity.DbLog;
import centwong.twitter.entity.Operation;
import centwong.twitter.entity.constant.KafkaTopicContant;
import centwong.twitter.entity.constant.SecurityConstant;
import centwong.twitter.entity.constant.UserConstant;
import centwong.twitter.app.user.repository.UserRepository;
import centwong.twitter.dto.UserDto;
import centwong.twitter.entity.User;
import centwong.twitter.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserService implements IUserService{

    private final UserRepository repository;

    private final IRedisRepository redisRepository;

    private final ReactiveElasticsearchOperations elasticRepository;

    private final Producer producer;

    private final BCryptPasswordEncoder bcrypt;

    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository repository, RedisRepository redisRepository, ReactiveElasticsearchOperations elasticRepository, Producer producer, BCryptPasswordEncoder bcrypt, JwtUtil util){
        this.repository = repository;
        this.redisRepository = redisRepository;
        this.elasticRepository = elasticRepository;
        this.producer = producer;
        this.bcrypt = bcrypt;
        this.jwtUtil = util;
    }

    @Override
    public Mono<User> create(UserDto.Create dto) {
        var user = dto.toUser();
        var insertUser = this
                .repository
                .findByEmailOrNoTelephone(dto.noTelephoneOrEmail())
                .flatMap((d) -> Mono.<User>error(new RuntimeException("Email sudah terdaftar")))
                .switchIfEmpty(
                        this
                                .repository
                                .save(user)
                                .flatMap(elasticRepository::save)
                );
        var deleteCache = this
                .redisRepository
                .deleteCache(UserConstant.ALL);
        return insertUser
                .zipWith(deleteCache)
                .map(Tuple2::getT1)
                .doOnSuccess((s) -> {
                    producer.sendMessage(
                            KafkaTopicContant.DB_LOG,
                            DbLog
                                    .builder()
                                    .operation(Operation.CREATE.name())
                                    .tableName(User.class.getName())
                                    .message(String.format("save %s with data %s", User.class.getSimpleName(), s))
                                    .build()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<User> login(UserDto.Login dto) {
        var user = dto.toUser();
        return this
                .repository
                .findByEmailOrNoTelephone(user.getNoTelephoneOrEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("Email atau nomor telepon tidak ditemukan")))
                .flatMap((d) -> {
                    if(this.bcrypt.matches(user.getPassword(), d.getPassword())){
                        var token = this.jwtUtil.generateToken(
                                AuthDto
                                        .builder()
                                        .id(d.getId())
                                        .principal(d.getName())
                                        .createdAt(d.getCreatedAt())
                                        .roles(List.of(String.format(SecurityConstant.AUTH_ROLE, SecurityConstant.Roles.USER)))
                                        .build()
                        );
                        d.setJwtToken(token);
                        return Mono.just(d);
                    } else {
                        return Mono.error(new RuntimeException("Kredensial tidak valid"));
                    }
                })
                .doOnError((e) -> e.printStackTrace())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
