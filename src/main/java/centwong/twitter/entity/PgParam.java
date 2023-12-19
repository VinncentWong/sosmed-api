package centwong.twitter.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PgParam {

    private Long limit;

    private Long page;
}
