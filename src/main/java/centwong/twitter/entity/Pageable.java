package centwong.twitter.entity;

import org.springframework.data.annotation.Transient;

public class Pageable {

    @Transient
    protected Long page;

    @Transient
    protected Long limit;
}
