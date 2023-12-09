package centwong.twitter.entity;

public record Response<T>(
        Boolean success,
        String message,
        T data,
        String jwtToken
) {}
