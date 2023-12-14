package centwong.twitter.app.user.repository;

import centwong.twitter.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    @Query("SELECT * FROM `user` WHERE no_telephone_email = :no_telephone_email")
    Mono<User> findByEmailOrNoTelephone(@Param("no_telephone_email") String emailOrNoTelephone);
}
