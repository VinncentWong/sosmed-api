package centwong.twitter.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Setter
@Getter
@ToString
public class UserParam {

    private Long id;

    private List<Long> ids;

    private String username;

    private String noTelephoneOrEmail;

    private PgParam pg;
}
