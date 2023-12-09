package centwong.twitter.util;

import centwong.twitter.entity.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<Response<T>> sendResponse(
            Boolean success,
            String message,
            T data,
            String jwtToken
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new Response<>(
                                success, message, data, jwtToken
                        )
                );
    }
}
