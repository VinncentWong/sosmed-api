package centwong.twitter.app.user.controller;

import centwong.twitter.app.user.service.IUserService;
import centwong.twitter.app.user.service.UserService;
import centwong.twitter.dto.UserDto;
import centwong.twitter.entity.Response;
import centwong.twitter.entity.User;
import centwong.twitter.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService service;

    @Autowired
    public UserController(UserService service){
        this.service = service;
    }

    @MutationMapping
    public Mono<ResponseEntity<Response<User>>> create(@Argument(name = "input") UserDto.Create dto){
        return this
                .service
                .create(dto)
                .map((res) -> ResponseUtil.sendResponse(
                        true,
                        "success create user",
                        res,
                        null
                ));
    }
}
