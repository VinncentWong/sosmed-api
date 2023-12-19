package centwong.twitter.app.user.service;

import centwong.twitter.app.broker.Producer;
import centwong.twitter.app.redis.IRedisRepository;
import centwong.twitter.app.redis.RedisRepository;
import centwong.twitter.app.user.repository.IUserRepository;
import centwong.twitter.app.user.repository.UserRepository;
import centwong.twitter.entity.*;
import centwong.twitter.entity.constant.KafkaTopicContant;
import centwong.twitter.entity.constant.SecurityConstant;
import centwong.twitter.entity.constant.UserRedisConstant;
import centwong.twitter.dto.UserDto;
import centwong.twitter.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserService implements IUserService{

    private final IUserRepository repository;

    private final IRedisRepository redisRepository;

    private final ReactiveElasticsearchOperations elasticRepository;

    private final Producer producer;

    private final BCryptPasswordEncoder bcrypt;

    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(IUserRepository repository, RedisRepository redisRepository, ReactiveElasticsearchOperations elasticRepository, Producer producer, BCryptPasswordEncoder bcrypt, JwtUtil util){
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
                .get(
                        UserParam
                                .builder()
                                .noTelephoneOrEmail(dto.noTelephoneOrEmail())
                                .build()
                )
                .flatMap((d) -> Mono.<User>error(new RuntimeException("Email sudah terdaftar")))
                .switchIfEmpty(
                        this
                                .repository
                                .save(user)
                                .flatMap(elasticRepository::save)
                );
        var deleteCache = this
                .redisRepository
                .deleteCache(UserRedisConstant.ALL);
        return insertUser
                .zipWith(deleteCache)
                .map(Tuple2::getT1)
                .doOnSuccess((s) -> {
                    producer.sendMessage(
                            KafkaTopicContant.DB_LOG,
                            DbLog
                                    .builder()
                                    .operation(Operation.CREATE.name())
                                    .tableName(User.class.getSimpleName())
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
                .get(
                        UserParam
                                .builder()
                                .noTelephoneOrEmail(dto.noTelephoneOrEmail())
                                .build()
                )
                .switchIfEmpty(Mono.error(new RuntimeException("Email atau nomor telepon tidak ditemukan")))
                .flatMap((d) -> {
                    if(this.bcrypt.matches(user.getPassword(), d.getPassword())){
                        var token = this.jwtUtil.generateToken(
                                AuthParam
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

    @Override
    public Mono<User> get(Long id) {
        return this
                .redisRepository
                .get(UserRedisConstant.GET, id, User.class)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on UserService.get(id)");
                    return this.repository
                            .get(
                                    UserParam
                                            .builder()
                                            .id(id)
                                            .build()
                            )
                            .flatMap((d) ->
                                    this
                                            .redisRepository
                                            .upsertCache(UserRedisConstant.GET, id, d, Duration.ofMinutes(1))
                                            .then(Mono.just(d))
                            );
                }))
                .doOnSuccess((d) -> {
                    this.producer
                            .sendMessage(KafkaTopicContant.DB_LOG,
                                    DbLog
                                            .builder()
                                            .operation(Operation.GET.name())
                                            .tableName(User.class.getSimpleName())
                                            .message(String.format("get user with id %d and result %s", id, d))
                                            .build()
                            );
                })
                .doOnError((e) -> e.printStackTrace())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<User>> getList(UserParam param) {
        return this
                .redisRepository
                .<User>getList(UserRedisConstant.GET_LIST, param)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on User.getList(param)");
                    return this.repository
                                    .getList(param)
                                    .flatMap((v) ->
                                            this
                                            .redisRepository
                                            .upsertCache(
                                                    UserRedisConstant.GET_LIST,
                                                    param,
                                                    v,
                                                    Duration.ofMinutes(1)
                                            )
                                            .then(Flux.fromIterable(v).collectList())
                                    );
                }))
                .doOnSuccess((d) -> {
                    producer.sendMessage(
                            KafkaTopicContant.DB_LOG,
                            DbLog
                                    .builder()
                                    .tableName(User.class.getSimpleName())
                                    .operation(Operation.GET_LIST.name())
                                    .message(String.format("successfully get list user with data: %s", d))
                                    .build()
                    );
                })
                .doOnError((e) -> e.printStackTrace())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> delete(Long id){
        return this
                .repository
                .update(id, User.builder().deletedAt(LocalDate.now()).build())
                .doOnSuccess((d) -> {
                    this.producer
                            .sendMessage(
                                    KafkaTopicContant.DB_LOG,
                                    DbLog
                                            .builder()
                                            .message(String.format("successfully delete user with id: %d", d.getId()))
                                            .operation(Operation.DELETE.name())
                                            .tableName(User.class.getSimpleName())
                                            .build()
                            );
                })
                .doOnError((e) -> e.printStackTrace())
                .subscribeOn(Schedulers.boundedElastic());
    }


}
