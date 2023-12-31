package centwong.twitter.app.user.controller;

import centwong.twitter.app.user.service.IUserService;
import centwong.twitter.app.user.service.UserService;
import centwong.twitter.dto.UserDto;
import centwong.twitter.entity.Response;
import centwong.twitter.entity.User;
import centwong.twitter.entity.UserParam;
import centwong.twitter.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class UserController {

    private final IUserService service;

    @Autowired
    public UserController(UserService service){
        this.service = service;
    }

    /***
     * <a href="https://github.com/graphql-java-kickstart/graphql-java-tools/discussions/589">...</a>
     * Don't use ResponseEntity becuase GraphQL Java Engine automatically send response with 3 props
     * If you want to customize, refer to link above
     */
    @MutationMapping
    public Mono<User> create(@Argument(name = "input") UserDto.Create dto){
        return this
                .service
                .create(dto);
    }

    @MutationMapping
    public Mono<User> login(@Argument(name = "input") UserDto.Login dto){
        return this
                .service
                .login(dto);
    }

    @QueryMapping
    public Mono<List<User>> getListUser(@Argument(name = "param") UserParam param){
        return this
                .service
                .getList(param);
    }

    @QueryMapping
    public Mono<User> getUser(@Argument(name = "id") Long id){
        return this
                .service
                .get(id);
    }

    @MutationMapping
    public Mono<User> deleteUser(@Argument(name = "id") Long id){
        return this
                .service
                .delete(id);
    }

    @MutationMapping
    public Mono<User> activateUser(@Argument(name = "id") Long id){
        return this
                .service
                .activate(id);
    }

    @MutationMapping
    public Mono<User> updateUser(@Argument(name = "id") Long id, @Argument(name = "input") User user){
        return this
                .service
                .update(id, user);
    }
}
