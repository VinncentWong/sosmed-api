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

    private long limit;

    private long page;

    private boolean isActive = true;
}
