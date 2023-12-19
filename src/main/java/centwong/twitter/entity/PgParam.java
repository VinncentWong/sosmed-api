package centwong.twitter.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class PgParam {

    private Long limit;

    private Long page;

    private boolean isActive = true;
}
