package centwong.twitter.dto;

import centwong.twitter.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UserDto {

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
                    // TODO: Apply Encryption on .password(this.password)
                    .password(this.password)
                    .build();
        }
    }
}
