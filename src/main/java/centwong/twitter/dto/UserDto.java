package centwong.twitter.dto;

import centwong.twitter.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Autowired
    private static BCryptPasswordEncoder encoder;

    public record Create(
        @NotNull(message = "email or telephone number should not be null")
        @NotBlank(message = "email or telephone number should not be empty")
        String noTelephoneOrEmail,

        @NotNull(message = "password should not be null")
        @NotBlank(message = "password should not be empty")
        String password
    ){
        public User toUser(){
            return User
                    .builder()
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .noTelephoneOrEmail(this.noTelephoneOrEmail)
                    .password(encoder.encode(this.password))
                    .build();
        }
    }
}
