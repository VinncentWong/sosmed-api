package centwong.twitter.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class AuthParam {
    private Long id;
    private String principal;
    private LocalDate createdAt;
    private List<String> roles;
}