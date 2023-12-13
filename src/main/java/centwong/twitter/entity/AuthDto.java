package centwong.twitter.entity;

import java.time.LocalDate;
import java.util.List;

public record AuthDto(
        Long id,
        String principal,
        LocalDate createdAt,
        List<String> roles
) {}
